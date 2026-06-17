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
        if (user == null)
            throw new NullPointerException("User cannot be null");
        if (user.hasTeam())
            throw new IllegalArgumentException("User already in a team");
    }

    /**
     * Modifica il profilo dell'utente.
     * Segue il sequence diagram di "Modifica Profilo":
     * 1. Verifica che i dati siano validi
     * 2. Recupera l'utente
     * 3. Verifica che la nuova email non sia già in uso
     * 4. Aggiorna e salva il profilo
     */
    public User updateProfile(Long userID, String name, String email) {
        // 1. Verifica dati validi
        if (userID == null || name == null || name.trim().isEmpty()
                || email == null || email.trim().isEmpty())
            throw new IllegalArgumentException("Invalid data");

        // 2. Recupera l'utente
        User user = userRepository.findByID(userID);
        if (user == null)
            throw new IllegalArgumentException("User not found");

        // 3. Verifica che la nuova email non sia già in uso da un altro utente
        User existingWithEmail = userRepository.findByEmail(email);
        if (existingWithEmail != null && !existingWithEmail.getId().equals(userID))
            throw new IllegalArgumentException("Email already in use");

        // 4. Aggiorna e salva
        user.setName(name);
        user.setEmail(email);
        userRepository.save(user);

        return user;
    }

    /**
     * Elimina il profilo dell'utente.
     * Segue il sequence diagram di "Elimina Profilo":
     * 1. Recupera l'utente
     * 2. Elimina il profilo
     */
    public void deleteProfile(Long userID) {
        if (userID == null)
            throw new IllegalArgumentException("User ID cannot be null");

        // 1. Recupera l'utente
        User user = userRepository.findByID(userID);
        if (user == null)
            throw new IllegalArgumentException("User not found");

        // 2. Elimina
        userRepository.delete(user);
    }
}