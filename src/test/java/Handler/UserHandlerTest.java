package Handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import unicam.hackhub.handler.UserHandler;
import unicam.hackhub.model.User;
import unicam.hackhub.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserHandlerTest {

    private UserService userService;
    private UserHandler userHandler;
    private User mockUser;

    @BeforeEach
    void setUp() {
        // Mocking del servizio e iniezione nel costruttore dell'handler
        userService = Mockito.mock(UserService.class);
        userHandler = new UserHandler(userService);
        mockUser = Mockito.mock(User.class);
    }

    // =======================================================================
    // 1. Test per updateProfile (PUT)
    // =======================================================================

    @Test
    @DisplayName("updateProfile – Successo restituisce 200 OK")
    void updateProfile_Success() {
        when(userService.updateProfile(1L, "Alice Updated", "alice.new@test.it")).thenReturn(mockUser);

        ResponseEntity<User> response = userHandler.updateProfile(1L, "Alice Updated", "alice.new@test.it");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockUser, response.getBody());
        verify(userService).updateProfile(1L, "Alice Updated", "alice.new@test.it");
    }

    @Test
    @DisplayName("updateProfile – Dati invalidi restituisce 400 Bad Request")
    void updateProfile_BadRequest() {
        when(userService.updateProfile(anyLong(), anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        ResponseEntity<User> response = userHandler.updateProfile(1L, "", "");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    // =======================================================================
    // 2. Test per deleteProfile (DELETE)
    // =======================================================================

    @Test
    @DisplayName("deleteProfile – Successo restituisce 240 No Content")
    void deleteProfile_Success() {
        doNothing().when(userService).deleteProfile(1L);

        ResponseEntity<Void> response = userHandler.deleteProfile(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService).deleteProfile(1L);
    }

    @Test
    @DisplayName("deleteProfile – Utente non trovato restituisce 404 Not Found")
    void deleteProfile_NotFound() {
        doThrow(new IllegalArgumentException("User not found"))
                .when(userService).deleteProfile(99L);

        ResponseEntity<Void> response = userHandler.deleteProfile(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}