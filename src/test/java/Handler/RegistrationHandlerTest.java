package Handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import unicam.hackhub.handler.RegistrationHandler;
import unicam.hackhub.model.Registration;
import unicam.hackhub.model.Team;
import unicam.hackhub.model.User;
import unicam.hackhub.service.RegistrationService;
import unicam.hackhub.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistrationHandlerTest {

    private RegistrationService registrationService;
    private UserService userService;
    private RegistrationHandler registrationHandler;

    private Registration mockRegistration;
    private Team mockTeam;
    private User mockUser;

    @BeforeEach
    void setUp() {
        // 1. Inizializzazione manuale e accoppiamento dei mock dei Service
        registrationService = Mockito.mock(RegistrationService.class);
        userService = Mockito.mock(UserService.class);

        // 2. Passiamo entrambi i service al costruttore dell'handler
        registrationHandler = new RegistrationHandler(registrationService, userService);

        // 3. Mock dei modelli di dati
        mockRegistration = Mockito.mock(Registration.class);
        mockTeam = Mockito.mock(Team.class);
        mockUser = Mockito.mock(User.class);
    }

    // =======================================================================
    // Test per registerTeam (POST)
    // =======================================================================

    @Test
    @DisplayName("registerTeam – Successo restituisce 201 Created")
    void registerTeam_Success() {
        Long hackathonId = 1L;
        Long userId = 2L;

        when(registrationService.registerTeam(hackathonId, userId)).thenReturn(mockRegistration);

        // Eseguiamo la chiamata diretta che restituisce ResponseEntity
        ResponseEntity<Registration> response = registrationHandler.registerTeam(hackathonId, userId);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockRegistration, response.getBody());
        verify(registrationService, times(1)).registerTeam(hackathonId, userId);
    }

    @Test
    @DisplayName("registerTeam – Eccezione nel Service restituisce 400 Bad Request")
    void registerTeam_Exception_ReturnsBadRequest() {
        Long hackathonId = 1L;
        Long userId = 2L;

        when(registrationService.registerTeam(hackathonId, userId))
                .thenThrow(new IllegalArgumentException("Hackathon non trovato"));

        ResponseEntity<Registration> response = registrationHandler.registerTeam(hackathonId, userId);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    // =======================================================================
    // Test per getRegistration (GET)
    // =======================================================================

    @Test
    @DisplayName("getRegistration – Trovata restituisce 200 OK")
    void getRegistration_Success() {
        Long teamId = 10L;

        when(registrationService.getRegistration(teamId)).thenReturn(mockRegistration);

        ResponseEntity<Registration> response = registrationHandler.getRegistration(teamId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockRegistration, response.getBody());
    }

    @Test
    @DisplayName("getRegistration – Null restituisce 404 Not Found")
    void getRegistration_NotFound_Returns404() {
        Long teamId = 10L;

        // Il controller verifica esplicitamente se l'oggetto restituito è null
        when(registrationService.getRegistration(teamId)).thenReturn(null);

        ResponseEntity<Registration> response = registrationHandler.getRegistration(teamId);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    // =======================================================================
    // Test per getTeamByUser (GET)
    // =======================================================================

    @Test
    @DisplayName("getTeamByUser – Team trovato per l'utente restituisce 200 OK")
    void getTeamByUser_Success() {
        Long userId = 2L;

        // L'handler prima cerca l'utente via UserService, poi cerca il team associato
        when(userService.getUser(userId)).thenReturn(mockUser);
        when(registrationService.getTeamByUser(mockUser)).thenReturn(mockTeam);

        ResponseEntity<Team> response = registrationHandler.getTeamByUser(userId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockTeam, response.getBody());
    }

    @Test
    @DisplayName("getTeamByUser – Team assente per l'utente restituisce 404 Not Found")
    void getTeamByUser_NotFound_Returns404() {
        Long userId = 2L;

        when(userService.getUser(userId)).thenReturn(mockUser);
        when(registrationService.getTeamByUser(mockUser)).thenReturn(null);

        ResponseEntity<Team> response = registrationHandler.getTeamByUser(userId);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}