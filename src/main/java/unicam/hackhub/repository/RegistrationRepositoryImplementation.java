package unicam.hackhub.repository;

import unicam.hackhub.model.Hackathon;
import unicam.hackhub.model.Registration;
import unicam.hackhub.model.Team;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RegistrationRepositoryImplementation implements RegistrationRepository {

    private final Set<Registration> registrations = new HashSet<Registration>();

    @Override
    public Registration findByID(Long registrationID) {
        return registrations.stream()
                .filter(r -> r.getId().equals(registrationID))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Registration findByTeamID(Long teamID) {
        return registrations.stream()
                .filter(r -> r.getTeam().getId().equals(teamID))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Registration findByRegistration(Team team, Hackathon hackathon) {
        return registrations.stream()
                .filter(r -> r.getTeam().equals(team) && r.getHackathon().equals(hackathon))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Registration save(Registration registration) {
        registrations.add(registration);
        return registration;
    }

    @Override
    public void saveAll(List<Registration> entities) {
        for(Registration registration : entities){
            registrations.add(registration);
        }
        System.out.println("Registrations saved");
    }

}
