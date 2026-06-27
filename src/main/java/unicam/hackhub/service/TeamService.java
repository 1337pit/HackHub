package unicam.hackhub.service;

import unicam.hackhub.model.*;
import unicam.hackhub.repository.HackathonRepository;
import unicam.hackhub.repository.TeamRepository;
import unicam.hackhub.repository.UserRepository;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

public class TeamService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final HackathonRepository hackathonRepository;
    private final InviteService inviteService;
    private final UserService userService;

    private static long idCounter = 1;

    public TeamService(UserRepository userRepository,
                       TeamRepository teamRepository, HackathonRepository hackathonRepository,
                       InviteService inviteService, UserService userService) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.hackathonRepository = hackathonRepository;
        this.inviteService = inviteService;
        this.userService = userService;
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
     * Crea una richiesta di supporto seguendo il flusso del sequence diagram:
     * 1. Verifica che i dati siano corretti
     * 2. Prende il mentore scelto dal team
     * 3. Prende lo slot scelto dal team
     * 4. Crea la richiesta di supporto
     */
    public RequestSupport requiresAssistance(Long id, Long hackathonID, Long userID, Long teamID, Long mentorID) {

        // 1. Verifica che i dati siano corretti
        if (id == null || hackathonID == null  || userID == null || teamID == null || mentorID == null)
            throw new IllegalArgumentException("Data cannot be null");

        // 2. Prende l'hackathon
        Hackathon hackathon = hackathonRepository.findByID(hackathonID);

        // 3. Prende la lista dei mentori dell'hackathon
        List<Mentor> listMentors = hackathon.getListMentors();

        // 4. Prende il mentore scelto dal membro del team
        Mentor chosenMentor = null;
        while (chosenMentor == null) {
            chosenMentor = hackathon.selectMentor(listMentors);
        }

        // 5. Prende la lista degli slot disponibili
        List<Mentor.Slot> listSlotsAvailable = chosenMentor.getSlotsAvailable(chosenMentor.getListSlots());

        // 6. Prende lo slot scelto dal membro del team
        Mentor.Slot chosenSlot = null;
        while (chosenSlot == null) {
            if (listSlotsAvailable.isEmpty()) {
                System.out.println("Tutti gli slot sono pieni");
                break;
            }
            chosenSlot = chosenMentor.selectSlot(listSlotsAvailable);
        }

        // 7. Crea la richiesta di supporto
        RequestSupport requestSupport = new RequestSupport(id, chosenSlot, hackathonID, userID, teamID, mentorID);

        System.out.println("Sent request support: " + requestSupport);

        return requestSupport;
    }

    public Team getTeamByUser(User user) {
        return teamRepository.findByUser(user);
    }

    public Team getTeamByID(Long teamID) {
        return teamRepository.findByID(teamID);
    }

}