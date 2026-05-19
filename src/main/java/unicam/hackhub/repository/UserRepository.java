package unicam.hackhub.repository;

import unicam.hackhub.model.User;

import java.util.List;

public interface UserRepository {

    public void findByID(Long userID);

    public User save(User entity);

    public void saveAll(List<User> entities);

}
