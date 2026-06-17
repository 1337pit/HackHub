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
import static org.mockito.Mockito.*;

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

    // -----------------------------------------------------------------------
    // updateProfile
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("updateProfile – dati validi → aggiorna e salva il profilo")
    void updateProfile_validData_updatesAndSavesUser() {
        User user = new User(1L, "Alice", "alice@test.it");
        when(userRepository.findByID(1L)).thenReturn(user);
        when(userRepository.findByEmail("alice.new@test.it")).thenReturn(null);

        User result = userService.updateProfile(1L, "Alice Updated", "alice.new@test.it");

        assertNotNull(result);
        assertEquals("Alice Updated", result.getName());
        assertEquals("alice.new@test.it", result.getEmail());

        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("updateProfile – nuova email coincide con quella attuale dell'utente → nessuna eccezione")
    void updateProfile_sameEmailAsOwner_noException() {
        User user = new User(1L, "Alice", "alice@test.it");
        when(userRepository.findByID(1L)).thenReturn(user);
        when(userRepository.findByEmail("alice@test.it")).thenReturn(user);

        User result = userService.updateProfile(1L, "Alice Updated", "alice@test.it");

        assertNotNull(result);
        assertEquals("Alice Updated", result.getName());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("updateProfile – userID null → IllegalArgumentException 'Invalid data'")
    void updateProfile_nullUserID_throwsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.updateProfile(null, "Alice", "alice@test.it"));

        assertEquals("Invalid data", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateProfile – name null o vuoto → IllegalArgumentException 'Invalid data'")
    void updateProfile_nullOrEmptyName_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.updateProfile(1L, null, "alice@test.it"));

        assertThrows(IllegalArgumentException.class,
                () -> userService.updateProfile(1L, "  ", "alice@test.it"));

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateProfile – email null o vuota → IllegalArgumentException 'Invalid data'")
    void updateProfile_nullOrEmptyEmail_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.updateProfile(1L, "Alice", null));

        assertThrows(IllegalArgumentException.class,
                () -> userService.updateProfile(1L, "Alice", "  "));

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateProfile – utente non trovato → IllegalArgumentException 'User not found'")
    void updateProfile_userNotFound_throwsException() {
        when(userRepository.findByID(99L)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.updateProfile(99L, "Alice", "alice@test.it"));

        assertEquals("User not found", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateProfile – email già in uso da un altro utente → IllegalArgumentException")
    void updateProfile_emailAlreadyUsedByAnotherUser_throwsException() {
        User user = new User(1L, "Alice", "alice@test.it");
        User otherUser = new User(2L, "Bob", "bob@test.it");

        when(userRepository.findByID(1L)).thenReturn(user);
        when(userRepository.findByEmail("bob@test.it")).thenReturn(otherUser);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.updateProfile(1L, "Alice", "bob@test.it"));

        assertEquals("Email already in use", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    // -----------------------------------------------------------------------
    // deleteProfile
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("deleteProfile – utente trovato → elimina il profilo")
    void deleteProfile_userFound_deletesUser() {
        User user = new User(1L, "Alice", "alice@test.it");
        when(userRepository.findByID(1L)).thenReturn(user);

        userService.deleteProfile(1L);

        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("deleteProfile – userID null → IllegalArgumentException")
    void deleteProfile_nullUserID_throwsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.deleteProfile(null));

        assertEquals("User ID cannot be null", ex.getMessage());
        verify(userRepository, never()).delete(any());
    }

    @Test
    @DisplayName("deleteProfile – utente non trovato → IllegalArgumentException 'User not found'")
    void deleteProfile_userNotFound_throwsException() {
        when(userRepository.findByID(99L)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.deleteProfile(99L));

        assertEquals("User not found", ex.getMessage());
        verify(userRepository, never()).delete(any());
    }
}