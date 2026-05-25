package unicam.hackhub.service;

import unicam.hackhub.model.Hackathon;
import unicam.hackhub.model.Registration;
import unicam.hackhub.model.Team;
import unicam.hackhub.model.User;
import unicam.hackhub.repository.HackathonRepository;
import unicam.hackhub.repository.RegistrationRepository;
import unicam.hackhub.repository.TeamRepository;
import unicam.hackhub.repository.UserRepository;

public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final TeamRepository teamRepository;
    private final HackathonRepository hackathonRepository;
    private final UserRepository userRepository;

    private final UserService userService;
    private final HackathonService hackathonService;

    public RegistrationService(RegistrationRepository registrationRepository,
                               TeamRepository teamRepository, HackathonRepository hackathonRepository,
                               UserRepository userRepository, UserService userService,
                               HackathonService hackathonService) {
        this.registrationRepository = registrationRepository;
        this.teamRepository = teamRepository;
        this.hackathonRepository = hackathonRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.hackathonService = hackathonService;
    }

    /**
     * Registra un team seguendo il flusso del sequence diagram:
     * 1. Recupera l'utente
     * 2. Verifica che l'utente non faccia parte di nessun team
     * 3. Prende il team dell'utente e l'hackathon
     * 4. Verifica che l'hackathon sia aperto e che la team size non superi il limite
     * 5. Crea la registrazione
     * 6. Verifica che il team non sia già registrato
     * 7. Salva la registrazione
     */
    public Registration registerTeam(Long hackathonID, Long userID) {

        // 1. Verifica che i parametri passati non siano nulli
        if(hackathonID == null || userID == null) {
            throw new IllegalArgumentException("hackathonID and userID cannot be null");
        }

        // 2. Recupera l'utente
        User user = userRepository.findByID(userID);
        userService.checkEligibility(user);

        // 3. Verifica che l'utente non faccia parte di nessun team
        if (!(user.hasTeam()))
            throw new IllegalArgumentException("User has no team");

        // 4. Prende il team dell'utente
        Team team = user.getCurrentTeam();

        // 5. Prende l'hackathon
        Hackathon hackathon = hackathonRepository.findByID(hackathonID);

        // 6. Verifica che l'hackathon sia aperto
        hackathonService.checkHackathonAvailability(hackathon);

        // 7. Verifica che la team size non superi il limite
        hackathonService.checkTeamSize(team, hackathon);

        // 8. Crea la registrazione
        Registration registration = registrationRepository.findByRegistration(team, hackathon);

        // 9. Verifca che il team non sia già registrato nell'hackathon
        hackathonService.checkTeamAlreadyRegistered(registration);

        // 10. Salva la registrazione
        registrationRepository.save(registration);

        return registration;
    }

    public Registration getRegistration(Long teamID) {
        if(teamID == null) {
            throw new IllegalArgumentException("teamID cannot be null");
        }
        return registrationRepository.findByTeamID(teamID);
    }

    public Team getTeamByUser(User user) {
        return teamRepository.findByUser(user);
    }

}
