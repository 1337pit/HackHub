package Handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unicam.hackhub.handler.UserHandler;
import unicam.hackhub.model.User;
import unicam.hackhub.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test unitari per UserHandler.
 *
 * Verifica che l'handler:
 *  - deleghi correttamente a UserService
 *  - gestisca le eccezioni restituendo null (updateProfile) o false (deleteProfile)
 */
@ExtendWith(MockitoExtension.class)
class UserHandlerTest {

    @Mock
    private UserService userService;

    private UserHandler userHandler;

    @BeforeEach
    void setUp() {
        userHandler = new UserHandler(userService);
    }

    // -----------------------------------------------------------------------
    // updateProfile - Happy path
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("updateProfile – profilo aggiornato con successo → restituisce l'utente")
    void updateProfile_success_returnsUser() {
        User expectedUser = new User(1L, "Alice Updated", "alice.new@test.it");
        when(userService.updateProfile(1L, "Alice Updated", "alice.new@test.it"))
                .thenReturn(expectedUser);

        User result = userHandler.updateProfile(1L, "Alice Updated", "alice.new@test.it");

        assertNotNull(result);
        assertEquals("Alice Updated", result.getName());
        assertEquals("alice.new@test.it", result.getEmail());
        verify(userService, times(1)).updateProfile(1L, "Alice Updated", "alice.new@test.it");
    }

    // -----------------------------------------------------------------------
    // updateProfile - Gestione errori: l'handler cattura IllegalArgumentException
    // e restituisce null
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("updateProfile – dati invalidi → handler restituisce null")
    void updateProfile_invalidData_returnsNull() {
        when(userService.updateProfile(anyLong(), any(), any()))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        User result = userHandler.updateProfile(1L, "", "");

        assertNull(result);
    }

    @Test
    @DisplayName("updateProfile – utente non trovato → handler restituisce null")
    void updateProfile_userNotFound_returnsNull() {
        when(userService.updateProfile(anyLong(), any(), any()))
                .thenThrow(new IllegalArgumentException("User not found"));

        User result = userHandler.updateProfile(99L, "Alice", "alice@test.it");

        assertNull(result);
    }

    @Test
    @DisplayName("updateProfile – email già in uso → handler restituisce null")
    void updateProfile_emailAlreadyInUse_returnsNull() {
        when(userService.updateProfile(anyLong(), any(), any()))
                .thenThrow(new IllegalArgumentException("Email already in use"));

        User result = userHandler.updateProfile(1L, "Alice", "bob@test.it");

        assertNull(result);
    }

    // -----------------------------------------------------------------------
    // updateProfile - Delega: l'handler non fa logica, la lascia tutta al service
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("updateProfile – i parametri vengono passati intatti al service")
    void updateProfile_parametersPassedThrough() {
        User expectedUser = new User(1L, "Alice", "alice@test.it");
        when(userService.updateProfile(1L, "Alice", "alice@test.it")).thenReturn(expectedUser);

        userHandler.updateProfile(1L, "Alice", "alice@test.it");

        verify(userService).updateProfile(1L, "Alice", "alice@test.it");
    }

    // -----------------------------------------------------------------------
    // deleteProfile - Happy path
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("deleteProfile – eliminazione riuscita → restituisce true")
    void deleteProfile_success_returnsTrue() {
        doNothing().when(userService).deleteProfile(1L);

        boolean result = userHandler.deleteProfile(1L);

        assertTrue(result);
        verify(userService, times(1)).deleteProfile(1L);
    }

    // -----------------------------------------------------------------------
    // deleteProfile - Gestione errori: l'handler cattura IllegalArgumentException
    // e restituisce false
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("deleteProfile – userID null → handler restituisce false")
    void deleteProfile_nullUserID_returnsFalse() {
        doThrow(new IllegalArgumentException("User ID cannot be null"))
                .when(userService).deleteProfile(null);

        boolean result = userHandler.deleteProfile(null);

        assertFalse(result);
    }

    @Test
    @DisplayName("deleteProfile – utente non trovato → handler restituisce false")
    void deleteProfile_userNotFound_returnsFalse() {
        doThrow(new IllegalArgumentException("User not found"))
                .when(userService).deleteProfile(99L);

        boolean result = userHandler.deleteProfile(99L);

        assertFalse(result);
    }

    // -----------------------------------------------------------------------
    // deleteProfile - Delega: l'handler non fa logica, la lascia tutta al service
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("deleteProfile – l'userID viene passato intatto al service")
    void deleteProfile_parameterPassedThrough() {
        userHandler.deleteProfile(1L);

        verify(userService).deleteProfile(1L);
    }
}