package unicam.hackhub.repository;

import unicam.hackhub.model.User;

import java.util.List;

public interface UserRepository {

    User findByID(Long userID);

    User findByName(String name);

    User save(User entity);

    void saveAll(List<User> entities);
}