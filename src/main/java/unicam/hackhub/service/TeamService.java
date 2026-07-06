package unicam.hackhub.service;

import org.springframework.stereotype.Service;
import unicam.hackhub.model.*;
import unicam.hackhub.repository.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TeamService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final HackathonRepository hackathonRepository;
    private final StaffMemberRepository staffMemberRepository;
    private final ReportRepository reportRepository;
    private final InviteService inviteService;
    private final UserService userService;
    private final CalendarService calendarService;

    public TeamService(UserRepository userRepository,
                       TeamRepository teamRepository,
                       HackathonRepository hackathonRepository,
                       StaffMemberRepository staffMemberRepository,
                       ReportRepository reportRepository,
                       InviteService inviteService,
                       UserService userService,
                       CalendarService calendarService) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.hackathonRepository = hackathonRepository;
        this.staffMemberRepository = staffMemberRepository;
        this.reportRepository = reportRepository;
        this.inviteService = inviteService;
        this.userService = userService;
        this.calendarService = calendarService;
    }

    /**
     * Crea un team.
     * 1. Recupera e valida l'utente leader
     * 2. Verifica che il nome non sia già usato
     * 3. Crea e salva il team
     * 4. Assegna il team al leader
     * 5. Invia inviti agli altri utenti
     */
    public Team createTeam(Long userID, String name, List<User> teamUserIDs) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Team name cannot be empty or blank");

        User leader = userRepository.findById(userID)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        userService.checkEligibility(leader);

        if (leader.hasTeam())
            throw new IllegalArgumentException("User already in a team");

        teamRepository.findByTeamName(name).ifPresent(t -> {
            throw new IllegalArgumentException("Team name is already used");
        });

        List<User> members = new ArrayList<>();
        members.add(leader);
        Team team = new Team(null, name, members);
        teamRepository.save(team);

        leader.setCurrentTeam(team);
        userRepository.save(leader);

        if (teamUserIDs != null) {
            for (User u : teamUserIDs) {
                if (u != null && u.getId().equals(userID)) continue;
                userRepository.findById(u.getId()).ifPresent(invitedUser ->
                        inviteService.createInvite(team.getId(), invitedUser));
            }
        }

        return team;
    }

    /**
     * Elimina un team.
     * 1. Verifica che i dati siano corretti
     * 2. Verifica che l'utente faccia parte del team
     * 3. Elimina il team
     */
    public void deleteTeam(Long userID, Long teamID) {
        if (teamID == null || userID == null)
            throw new IllegalArgumentException("teamID and userID cannot be null");

        Team team = teamRepository.findById(teamID)
                .orElseThrow(() -> new IllegalArgumentException("Team does not exist"));

        User user = userRepository.findById(userID)
                .orElseThrow(() -> new IllegalArgumentException("User does not exist"));

        Team teamUser = user.getCurrentTeam();
        if (teamUser == null)
            throw new IllegalArgumentException("User is not in a team");
        if (!teamUser.getId().equals(teamID))
            throw new IllegalArgumentException("User is not in this team");
        if (team.getMembers() != null) {
            for (User member : team.getMembers()) {
                member.setCurrentTeam(null);
                userRepository.save(member);
            }
        }

        teamRepository.delete(team);
    }

    /**
     * Bandisce un team tramite il suo ID.
     */
    public void banTeam(Long teamID) {
        if (teamID == null)
            throw new IllegalArgumentException("Team ID cannot be null");

        Team team = teamRepository.findById(teamID)
                .orElseThrow(() -> new IllegalArgumentException("Team does not exist"));

        banTeam(team);
    }

    public void banTeam(Team team) {
        if (team == null)
            throw new IllegalArgumentException("Team cannot be null");

        if (team.getMembers() != null) {
            for (User member : team.getMembers()) {
                member.setCurrentTeam(null);
                userRepository.save(member);
            }
        }

        team.setMembers(new ArrayList<>());
        teamRepository.save(team);
    }

    /**
     * Segnala un team.
     */
    public Report reportTeam(Long mentorID, Long teamID, String description) {

        // 1. Verifica che i dati siano validi
        if (mentorID == null || teamID == null
                || description == null || description.trim().isEmpty())
            throw new IllegalArgumentException("Invalid data");

        // 2. Prende il mentore
        Mentor mentor = (Mentor) staffMemberRepository.findById(mentorID)
                .filter(s -> s instanceof Mentor)
                .orElseThrow(() -> new IllegalArgumentException("Mentor not found"));

        // 3. Prende il team da segnalare
        Team team = teamRepository.findById(teamID)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));

        // 4. Prende l'hackathon che contiene il team da segnalare
        Hackathon hackathon = team.getHackathon();
        if (hackathon == null)
            throw new IllegalArgumentException("Team is not registered to any hackathon");

        // 5. Verifica che il mentore sia assegnato all'hackathon del team
        boolean mentorAssigned = hackathon.getListMentors().stream()
                .anyMatch(m -> m.getId().equals(mentorID));
        if (!mentorAssigned)
            throw new IllegalArgumentException("Mentor is not assigned to this hackathon");

        // 6. Crea e salva la segnalazione
        Report report = new Report(null, description, team, mentor, hackathon);
        return reportRepository.save(report);
    }

    /**
     * Modifica il nome di un team.
     */
    public Team editTeamInfo(Long userID, Long teamID, String newName) {
        if (userID == null || teamID == null || newName == null || newName.isBlank())
            throw new IllegalArgumentException("Invalid team data");

        User user = userRepository.findById(userID)
                .orElseThrow(() -> new IllegalArgumentException("Invalid team data"));
        Team team = teamRepository.findById(teamID)
                .orElseThrow(() -> new IllegalArgumentException("Invalid team data"));

        Team currentTeam = user.getCurrentTeam();
        if (currentTeam == null || !teamID.equals(currentTeam.getId()))
            throw new IllegalArgumentException("User is not in this team");

        teamRepository.findByTeamName(newName)
                .filter(t -> !t.getId().equals(teamID))
                .ifPresent(t -> { throw new IllegalArgumentException("Team name already used"); });

        team.setTeamName(newName);
        return teamRepository.save(team);
    }

    /**
     * Crea una richiesta di supporto delegando a CalendarService.
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
        return teamRepository.findByUser(user).orElse(null);
    }

    public Team getTeamByID(Long teamID) {
        return teamRepository.findById(teamID).orElse(null);
    }
}