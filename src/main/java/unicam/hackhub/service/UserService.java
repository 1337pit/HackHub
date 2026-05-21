package unicam.hackhub.service;

import unicam.hackhub.model.User;
import unicam.hackhub.repository.UserRepository;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(Long userID) {
        User user = userRepository.findByID(userID);
        if (user == null)
            throw new IllegalArgumentException("User not found");
        return user;
    }

    public void checkEligibility(User user) {
        if (user == null) {
            throw new NullPointerException("User cannot be null");
        }
        if (user.hasTeam()) {
            throw new IllegalArgumentException("User already in a team");
        }
    }
}
