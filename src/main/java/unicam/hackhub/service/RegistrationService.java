package unicam.hackhub.service;

import org.springframework.stereotype.Service;
import unicam.hackhub.model.Hackathon;
import unicam.hackhub.model.Registration;
import unicam.hackhub.model.Team;
import unicam.hackhub.model.User;
import unicam.hackhub.repository.HackathonRepository;
import unicam.hackhub.repository.RegistrationRepository;
import unicam.hackhub.repository.TeamRepository;
import unicam.hackhub.repository.UserRepository;

@Service
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final TeamRepository teamRepository;
    private final HackathonRepository hackathonRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final HackathonService hackathonService;

    public RegistrationService(RegistrationRepository registrationRepository,
                               TeamRepository teamRepository,
                               HackathonRepository hackathonRepository,
                               UserRepository userRepository,
                               UserService userService,
                               HackathonService hackathonService) {
        this.registrationRepository = registrationRepository;
        this.teamRepository = teamRepository;
        this.hackathonRepository = hackathonRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.hackathonService = hackathonService;
    }

    /**
     * Registra un team a un hackathon.
     * 1. Recupera l'utente e verifica eligibilità
     * 2. Recupera il team dell'utente
     * 3. Recupera l'hackathon e verifica apertura e team size
     * 4. Verifica che il team non sia già registrato
     * 5. Salva la registrazione
     */
    public Registration registerTeam(Long hackathonID, Long userID) {
        if (hackathonID == null || userID == null)
            throw new IllegalArgumentException("hackathonID and userID cannot be null");

        User user = userRepository.findById(userID)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.hasTeam())
            throw new IllegalArgumentException("User has no team");

        Team team = user.getCurrentTeam();

        Hackathon hackathon = hackathonRepository.findById(hackathonID)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon not found"));

        hackathonService.checkHackathonAvailability(hackathon);
        hackathonService.checkTeamSize(team, hackathon);

        Registration existing = registrationRepository
                .findByTeamAndHackathon(team, hackathon).orElse(null);
        hackathonService.checkTeamAlreadyRegistered(existing);

        team.setHackathon(hackathon);
        teamRepository.save(team);

        Registration registration = new Registration(null, team, hackathon);
        return registrationRepository.save(registration);
    }

    public Registration getRegistration(Long teamID) {
        if (teamID == null)
            throw new IllegalArgumentException("teamID cannot be null");

        Team team = teamRepository.findById(teamID)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));

        return registrationRepository.findByTeam(team).orElse(null);
    }

    public Team getTeamByUser(User user) {
        return teamRepository.findByUser(user).orElse(null);
    }
}