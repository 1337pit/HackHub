package unicam.hackhub.service;

import unicam.hackhub.model.*;
import unicam.hackhub.repository.HackathonRepository;
import unicam.hackhub.repository.TeamRepository;
import unicam.hackhub.repository.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TeamService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final HackathonRepository hackathonRepository;
    private final InviteService inviteService;
    private final UserService userService;
    private final CalendarService calendarService;

    private static long idCounter = 1;

    public TeamService(UserRepository userRepository,
                       TeamRepository teamRepository,
                       HackathonRepository hackathonRepository,
                       InviteService inviteService,
                       UserService userService,
                       CalendarService calendarService) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.hackathonRepository = hackathonRepository;
        this.inviteService = inviteService;
        this.userService = userService;
        this.calendarService = calendarService;
    }

    /**
     * Crea un team seguendo il flusso del sequence diagram:
     * 1. Recupera e valida l'utente leader
     * 2. Verifica che il nome non sia già usato
     * 3. Crea e salva il team
     * 4. Assegna il team al leader
     * 5. Invia inviti agli altri utenti
     */
    public Team createTeam(Long userID, String name, List<User> teamUserIDs) {
        // 1. Validazione del nome del team (Risolve il secondo fallimento)
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Team name cannot be empty or blank");
        }

        // 2. Recupera utente
        User leader = userRepository.findByID(userID);
        userService.checkEligibility(leader);

        // 3. Check eligibilità
        if (leader.hasTeam())
            throw new IllegalArgumentException("User already in a team");

        // 4. Check nome team duplicato
        if (teamRepository.findByName(name) != null)
            throw new IllegalArgumentException("Team name is already used");

        // 5. Crea il team con il leader come primo membro
        List<User> members = new ArrayList<>();
        members.add(leader);
        Team team = new Team(idCounter++, name, members);

        // 6. Salva il team
        teamRepository.save(team);

        // 7. Aggiorna l'utente leader
        leader.setCurrentTeam(team);
        userRepository.save(leader);

        // 8. Invia inviti agli altri utenti
        if (teamUserIDs != null) {
            for (User u : teamUserIDs) {
                // Salta il leader se è presente nella lista (Risolve il primo fallimento)
                if (u != null && u.getId().equals(userID)) {
                    continue;
                }

                User invitedUser = userRepository.findByID(u.getId());
                if (invitedUser != null) {
                    inviteService.createInvite(team.getId(), invitedUser);
                }
            }
        }

        return team;
    }

    /**
     * Elimina un team seguendo il flusso del sequence diagram:
     * 1. Verifica che i dati siano corretti
     * 2. Prende il team e il membro del team
     * 3. Verifica che il membro del team sia presente nel team che vuole eliminare
     * 4. Elimina il team
     */
    public void deleteTeam(Long teamID, Long userID) {

        // 1. Verifica che i dati siano corretti
        if(teamID == null || userID == null)
            throw new IllegalArgumentException("teamID and userID cannot be null");

        // 2. Prende il team
        Team team = teamRepository.findByID(teamID);
        if (team == null) {
            throw new IllegalArgumentException("Team does not exist");
        }

        // 3. Prende il membro del team
        User user = userRepository.findByID(userID);
        if (user == null) {
            throw new IllegalArgumentException("User does not exist");
        }

        // 4. Verifica che il membro del team sia presente nel team che vuole eliminare
        Team teamUser = user.getCurrentTeam();
        if (teamUser == null) {
            throw new IllegalArgumentException("User is not in a team");
        }
        if (!teamUser.getId().equals(teamID)) {
            throw new IllegalArgumentException("User is not in this team");
        }

        // 5. Elimina il team
        teamRepository.delete(team);

        // 6. Visualizza notifica di successo
        System.out.println("Team has been deleted");

    }

    /**
     * Bandisce un team tramite il suo ID.
     *
     * @param teamID ID del team da bandire
     */
    public void banTeam(Long teamID) {
        if (teamID == null) {
            throw new IllegalArgumentException("Team ID cannot be null");
        }

        Team team = teamRepository.findByID(teamID);

        if (team == null) {
            throw new IllegalArgumentException("Team does not exist");
        }

        banTeam(team);
    }

    /**
     * Rimuove un team (usato da Organizer.banTeam).
     */
    public void banTeam(Team team) {
        if (team == null)
            throw new IllegalArgumentException("Team cannot be null");

        // Rimuove il team da tutti i membri
        if (team.getMembers() != null) {
            for (User member : team.getMembers()) {
                member.setCurrentTeam(null);
                userRepository.save(member);
            }
        }

        // Salva il team aggiornato (senza membri)
        team.setMembers(new ArrayList<>());
        teamRepository.save(team);
    }

    /**
     * Modifica il nome di un team.
     *
     * @param userID  ID dell'utente che richiede la modifica
     * @param teamID  ID del team
     * @param newName nuovo nome del team
     * @return team modificato
     */
    public Team editTeamInfo(Long userID, Long teamID, String newName) {
        if (userID == null
                || teamID == null
                || newName == null
                || newName.isBlank()) {
            throw new IllegalArgumentException("Invalid team data");
        }

        User user = userRepository.findByID(userID);
        Team team = teamRepository.findByID(teamID);

        if (user == null || team == null) {
            throw new IllegalArgumentException("Invalid team data");
        }

        Team currentTeam = user.getCurrentTeam();

        if (currentTeam == null
                || !teamID.equals(currentTeam.getId())) {
            throw new IllegalArgumentException("User is not in this team");
        }

        Team existingTeam = teamRepository.findByName(newName);

        if (existingTeam != null
                && !teamID.equals(existingTeam.getId())) {
            throw new IllegalArgumentException("Team name already used");
        }

        team.setTeamName(newName);

        return teamRepository.save(team);
    }

    /**
     * Crea una richiesta di supporto seguendo il flusso del sequence diagram:
     * 1. Verifica che i dati siano corretti
     * 2. Delega a CalendarService la verifica disponibilità e la prenotazione
     *    su Google Calendar
     */
    public SupportRequest requiresAssistance(Long hackathonID, Long userID,
                                             Long teamID, Long mentorID,
                                             LocalDate date) {
        if (hackathonID == null || userID == null || teamID == null
                || mentorID == null || date == null)
            throw new IllegalArgumentException("Data cannot be null");

        return calendarService.bookDate(mentorID, teamID, hackathonID, userID, date);
    }

    public Team getTeamByUser(User user) {
        return teamRepository.findByUser(user);
    }

    public Team getTeamByID(Long teamID) {
        return teamRepository.findByID(teamID);
    }

}