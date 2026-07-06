package unicam.hackhub.service;

import org.springframework.stereotype.Service;
import unicam.hackhub.model.User;
import unicam.hackhub.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(Long userID) {
        return userRepository.findById(userID)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public void checkEligibility(User user) {
        if (user == null)
            throw new NullPointerException("User cannot be null");
        if (user.hasTeam())
            throw new IllegalArgumentException("User already in a team");
    }

    /**
     * Modifica il profilo dell'utente.
     * 1. Verifica che i dati siano validi
     * 2. Recupera l'utente
     * 3. Verifica che la nuova email non sia già in uso
     * 4. Aggiorna e salva il profilo
     */
    public User updateProfile(Long userID, String name, String email) {
        if (userID == null || name == null || name.trim().isEmpty()
                || email == null || email.trim().isEmpty())
            throw new IllegalArgumentException("Invalid data");

        User user = userRepository.findById(userID)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        userRepository.findByEmail(email)
                .filter(u -> !u.getId().equals(userID))
                .ifPresent(u -> { throw new IllegalArgumentException("Email already in use"); });

        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    /**
     * Elimina il profilo dell'utente.
     * 1. Recupera l'utente
     * 2. Elimina il profilo
     */
    public void deleteProfile(Long userID) {
        if (userID == null)
            throw new IllegalArgumentException("User ID cannot be null");

        User user = userRepository.findById(userID)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        userRepository.delete(user);
    }
}