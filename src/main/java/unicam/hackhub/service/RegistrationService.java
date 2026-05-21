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

    public Registration registerTeam(Long hackathonID, Long userID) {
        if(hackathonID == null || userID == null) {
            throw new IllegalArgumentException("hackathonID and userID cannot be null");
        }

        User user = userRepository.findByID(userID);
        userService.checkEligibility(user);

        if (!(user.hasTeam()))
            throw new IllegalArgumentException("User has no team");

        Team team = user.getCurrentTeam();

        Hackathon hackathon = hackathonRepository.findByID(hackathonID);

        hackathonService.checkHackathonAvailability(hackathon);

        hackathonService.checkTeamSize(team, hackathon);

        Registration registration = registrationRepository.findByRegistration(team, hackathon);

        hackathonService.checkTeamAlreadyRegistered(registration);

        return registrationRepository.save(registration);
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
