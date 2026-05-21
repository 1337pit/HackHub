package Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unicam.hackhub.model.Team;
import unicam.hackhub.model.User;
import unicam.hackhub.repository.UserRepository;
import unicam.hackhub.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Test unitari per UserService.
 *
 * Copre i metodi usati dal caso d'uso createTeam:
 *  - getUser(Long userID)
 *  - checkEligibility(User user)
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }

    // -----------------------------------------------------------------------
    // getUser
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getUser – utente trovato → restituisce l'utente")
    void getUser_found_returnsUser() {
        User user = new User(1L, "Alice", "alice@test.it");
        when(userRepository.findByID(1L)).thenReturn(user);

        User result = userService.getUser(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Alice", result.getName());
    }

    @Test
    @DisplayName("getUser – utente non trovato → IllegalArgumentException 'User not found'")
    void getUser_notFound_throwsException() {
        when(userRepository.findByID(99L)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.getUser(99L));

        assertEquals("User not found", ex.getMessage());
    }

    // -----------------------------------------------------------------------
    // checkEligibility
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("checkEligibility – utente senza team → nessuna eccezione")
    void checkEligibility_userWithoutTeam_noException() {
        User user = new User(1L, "Alice", "alice@test.it");
        // user.hasTeam() == false → nessuna eccezione attesa

        assertDoesNotThrow(() -> userService.checkEligibility(user));
    }

    @Test
    @DisplayName("checkEligibility – utente già in un team → IllegalArgumentException")
    void checkEligibility_userAlreadyInTeam_throwsException() {
        User user = new User(1L, "Alice", "alice@test.it");
        user.setCurrentTeam(new Team(10L, "ExistingTeam", List.of(user)));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.checkEligibility(user));

        assertEquals("User already in a team", ex.getMessage());
    }

    @Test
    @DisplayName("checkEligibility – utente null → NullPointerException (bug noto)")
    void checkEligibility_nullUser_throwsNPE() {
        // Comportamento attuale: user.hasTeam() → NullPointerException
        // Fix suggerito: aggiungere un null-check all'inizio del metodo
        assertThrows(NullPointerException.class,
                () -> userService.checkEligibility(null));
    }
}
