package unicam.hackhub.handler;

import unicam.hackhub.model.User;
import unicam.hackhub.service.UserService;

public class UserHandler {

    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    /**
     * Gestisce la richiesta di modifica profilo.
     */
    public User updateProfile(Long userID, String name, String email) {
        try {
            return userService.updateProfile(userID, name, email);
        } catch (IllegalArgumentException e) {
            System.err.println("updateProfile error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Gestisce la richiesta di eliminazione profilo.
     */
    public boolean deleteProfile(Long userID) {
        try {
            userService.deleteProfile(userID);
            return true;
        } catch (IllegalArgumentException e) {
            System.err.println("deleteProfile error: " + e.getMessage());
            return false;
        }
    }
}