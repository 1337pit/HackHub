package Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unicam.hackhub.model.Team;
import unicam.hackhub.model.User;
import unicam.hackhub.repository.UserRepository;
import unicam.hackhub.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User(1L, "Alice", "alice@test.it");
    }

    // =========================================================================
    // 1. Test per getUser
    // =========================================================================

    @Test
    @DisplayName("getUser – utente trovato → restituisce l'utente")
    void getUser_found_returnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        User result = userService.getUser(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Alice", result.getName());
    }

    @Test
    @DisplayName("getUser – utente non trovato → IllegalArgumentException 'User not found'")
    void getUser_notFound_throwsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.getUser(99L));

        assertEquals("User not found", ex.getMessage());
    }

    // =========================================================================
    // 2. Test per createUser
    // =========================================================================

    @Test
    @DisplayName("createUser – dati validi ed email libera → crea e salva l'utente")
    void createUser_Success() {
        when(userRepository.findByEmail("new@test.it")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User result = userService.createUser("Alice", "new@test.it");

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("createUser – email già presente a sistema → lancia IllegalArgumentException")
    void createUser_EmailAlreadyInUse_ThrowsException() {
        when(userRepository.findByEmail("alice@test.it")).thenReturn(Optional.of(mockUser));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.createUser("Alice", "alice@test.it"));

        assertEquals("Email already in use", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    // =========================================================================
    // 3. Test per checkEligibility
    // =========================================================================

    @Test
    @DisplayName("checkEligibility – utente senza team → nessuna eccezione")
    void checkEligibility_userWithoutTeam_noException() {
        // mockUser di base ha hasTeam() == false
        assertDoesNotThrow(() -> userService.checkEligibility(mockUser));
    }

    @Test
    @DisplayName("checkEligibility – utente già in un team → IllegalArgumentException")
    void checkEligibility_userAlreadyInTeam_throwsException() {
        mockUser.setCurrentTeam(new Team(10L, "ExistingTeam", List.of(mockUser)));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.checkEligibility(mockUser));

        assertEquals("User already in a team", ex.getMessage());
    }

    @Test
    @DisplayName("checkEligibility – utente null → NullPointerException con messaggio esplicito")
    void checkEligibility_nullUser_throwsNPE() {
        NullPointerException ex = assertThrows(NullPointerException.class,
                () -> userService.checkEligibility(null));

        assertEquals("User cannot be null", ex.getMessage());
    }

    // =========================================================================
    // 4. Test per updateProfile
    // =========================================================================

    @Test
    @DisplayName("updateProfile – dati validi → aggiorna e salva il profilo")
    void updateProfile_validData_updatesAndSavesUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.findByEmail("alice.new@test.it")).thenReturn(Optional.empty());
        when(userRepository.save(mockUser)).thenReturn(mockUser);

        User result = userService.updateProfile(1L, "Alice Updated", "alice.new@test.it");

        assertNotNull(result);
        assertEquals("Alice Updated", result.getName());
        assertEquals("alice.new@test.it", result.getEmail());
        verify(userRepository).save(mockUser);
    }

    @Test
    @DisplayName("updateProfile – la nuova email coincide con la propria attuale → nessuna eccezione di duplicato")
    void updateProfile_sameEmailAsOwner_noException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.findByEmail("alice@test.it")).thenReturn(Optional.of(mockUser));
        when(userRepository.save(mockUser)).thenReturn(mockUser);

        User result = userService.updateProfile(1L, "Alice Updated", "alice@test.it");

        assertNotNull(result);
        assertEquals("Alice Updated", result.getName());
        verify(userRepository).save(mockUser);
    }

    @Test
    @DisplayName("updateProfile – dati mancanti o non validi → IllegalArgumentException 'Invalid data'")
    void updateProfile_invalidData_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> userService.updateProfile(null, "Alice", "alice@test.it"));
        assertThrows(IllegalArgumentException.class, () -> userService.updateProfile(1L, "  ", "alice@test.it"));
        assertThrows(IllegalArgumentException.class, () -> userService.updateProfile(1L, "Alice", ""));

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateProfile – email già in uso da un altro utente della piattaforma → Lancia eccezione")
    void updateProfile_emailAlreadyUsedByAnotherUser_throwsException() {
        User otherUser = new User(2L, "Bob", "bob@test.it");

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.findByEmail("bob@test.it")).thenReturn(Optional.of(otherUser)); // Restituisce ID 2L

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.updateProfile(1L, "Alice", "bob@test.it"));

        assertEquals("Email already in use", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    // =========================================================================
    // 5. Test per deleteProfile
    // =========================================================================

    @Test
    @DisplayName("deleteProfile – utente trovato → elimina correttamente il profilo")
    void deleteProfile_userFound_deletesUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        userService.deleteProfile(1L);

        verify(userRepository).delete(mockUser);
    }

    @Test
    @DisplayName("deleteProfile – userID null → IllegalArgumentException")
    void deleteProfile_nullUserID_throwsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.deleteProfile(null));

        assertEquals("User ID cannot be null", ex.getMessage());
        verify(userRepository, never()).delete(any());
    }
}