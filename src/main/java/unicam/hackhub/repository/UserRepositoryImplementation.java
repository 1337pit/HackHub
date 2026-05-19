package unicam.hackhub.repository;

import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties;
import unicam.hackhub.model.User;

import java.util.List;
import java.util.Set;

public class UserRepositoryImplementation {

    private Set<User> users;

    public void findByID(Long userID) {}

    public User save(User entity) {return null;}

    public void saveAll(List<User> entities) {}

}
