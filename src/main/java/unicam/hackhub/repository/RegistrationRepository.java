package unicam.hackhub.repository;

import unicam.hackhub.model.Registration;

import java.util.List;

public interface RegistrationRepository {

    public void findByID(Long registrationID);

    public Registration save(Registration entity);

    public void saveAll(List<Registration> entities);

}
