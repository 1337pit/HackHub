package unicam.hackhub.handler;

import unicam.hackhub.model.Registration;
import unicam.hackhub.model.Team;
import unicam.hackhub.model.User;
import unicam.hackhub.service.RegistrationService;
import unicam.hackhub.service.TeamService;

public class RegistrationHandler {

    private final RegistrationService registrationService;

    public RegistrationHandler(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    public Registration registerTeam(Long hackathonID, Long userID) {
        try {
            return registrationService.registerTeam(hackathonID, userID);
        } catch (IllegalArgumentException e) {
            System.err.println("registerTeam error: " + e.getMessage());
            return null;
        }
    }

    public Registration getRegistration(Long teamID) {
        try {
            return registrationService.getRegistration(teamID);
        } catch (IllegalArgumentException e) {
            System.err.println("getRegistration error: " + e.getMessage());
            return null;
        }
    }

    public Team getTeamByUser(User user) {
        try {
            return registrationService.getTeamByUser(user);
        } catch (IllegalArgumentException e) {
            System.err.println("getTeamByUser error: " + e.getMessage());
            return null;
        }
    }

}
