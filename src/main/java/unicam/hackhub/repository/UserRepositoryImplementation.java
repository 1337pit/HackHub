package unicam.hackhub.repository;

import unicam.hackhub.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserRepositoryImplementation implements UserRepository {

    private final List<User> users = new ArrayList<>();

    @Override
    public User findByID(Long userID) {
        return users.stream()
                .filter(u -> u.getId().equals(userID))
                .findFirst()
                .orElse(null);
    }

    @Override
    public User findByName(String name) {
        return users.stream()
                .filter(u -> u.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public User save(User entity) {
        users.removeIf(u -> u.getId().equals(entity.getId()));
        users.add(entity);
        return entity;
    }

    @Override
    public void saveAll(List<User> entities) {
        entities.forEach(this::save);
    }
}