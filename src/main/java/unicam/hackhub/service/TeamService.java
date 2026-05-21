package unicam.hackhub.service;

import unicam.hackhub.model.Team;
import unicam.hackhub.model.User;
import unicam.hackhub.repository.TeamRepository;
import unicam.hackhub.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class TeamService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final InviteService inviteService;
    private final UserService userService;

    private static long idCounter = 1;

    public TeamService(UserRepository userRepository,
                       TeamRepository teamRepository,
                       InviteService inviteService, UserService userService) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
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

    public Team getTeamByUser(User user) {
        return teamRepository.findByUser(user);
    }

    public Team getTeamByID(Long teamID) {
        return teamRepository.findByID(teamID);
    }

}