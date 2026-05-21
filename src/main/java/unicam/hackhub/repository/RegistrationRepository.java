package unicam.hackhub.repository;

import unicam.hackhub.model.Hackathon;
import unicam.hackhub.model.Registration;
import unicam.hackhub.model.Team;

import java.util.List;

public interface RegistrationRepository {

    public Registration findByID(Long registrationID);

    public Registration findByTeamID(Long teamID);

    public Registration findByRegistration(Team team, Hackathon hackathon);

    public Registration save(Registration entity);

    public void saveAll(List<Registration> entities);

}
