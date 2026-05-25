package Handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import unicam.hackhub.handler.RegistrationHandler;
import unicam.hackhub.model.Registration;
import unicam.hackhub.model.Team;
import unicam.hackhub.model.User;
import unicam.hackhub.service.RegistrationService;

@ExtendWith(MockitoExtension.class)
public class RegistrationHandlerTest {

    @Mock
    private RegistrationService registrationService;

    @InjectMocks
    private RegistrationHandler registrationHandler;

    private Registration mockRegistration;
    private Team mockTeam;
    private User mockUser;

    @BeforeEach
    void setUp() {
        // Inizializzazione degli oggetti mock per i test
        mockRegistration = mock(Registration.class);
        mockTeam = mock(Team.class);
        mockUser = mock(User.class);
    }

    // --- Test per registerTeam ---

    @Test
    void registerTeam_Success() {
        Long hackathonId = 1L;
        Long userId = 2L;

        when(registrationService.registerTeam(hackathonId, userId)).thenReturn(mockRegistration);

        Registration result = registrationHandler.registerTeam(hackathonId, userId);

        assertNotNull(result);
        assertEquals(mockRegistration, result);
        verify(registrationService, times(1)).registerTeam(hackathonId, userId);
    }

    @Test
    void registerTeam_Exception_ReturnsNull() {
        Long hackathonId = 1L;
        Long userId = 2L;

        when(registrationService.registerTeam(hackathonId, userId))
                .thenThrow(new IllegalArgumentException("Hackathon non trovato"));

        Registration result = registrationHandler.registerTeam(hackathonId, userId);

        assertNull(result);
        verify(registrationService, times(1)).registerTeam(hackathonId, userId);
    }

    // --- Test per getRegistration ---

    @Test
    void getRegistration_Success() {
        Long teamId = 10L;

        when(registrationService.getRegistration(teamId)).thenReturn(mockRegistration);

        Registration result = registrationHandler.getRegistration(teamId);

        assertNotNull(result);
        assertEquals(mockRegistration, result);
        verify(registrationService, times(1)).getRegistration(teamId);
    }

    @Test
    void getRegistration_Exception_ReturnsNull() {
        Long teamId = 10L;

        when(registrationService.getRegistration(teamId))
                .thenThrow(new IllegalArgumentException("Registrazione inesistente"));

        Registration result = registrationHandler.getRegistration(teamId);

        assertNull(result);
        verify(registrationService, times(1)).getRegistration(teamId);
    }

    // --- Test per getTeamByUser ---

    @Test
    void getTeamByUser_Success() {
        when(registrationService.getTeamByUser(mockUser)).thenReturn(mockTeam);

        Team result = registrationHandler.getTeamByUser(mockUser);

        assertNotNull(result);
        assertEquals(mockTeam, result);
        verify(registrationService, times(1)).getTeamByUser(mockUser);
    }

    @Test
    void getTeamByUser_Exception_ReturnsNull() {
        when(registrationService.getTeamByUser(mockUser))
                .thenThrow(new IllegalArgumentException("Utente non associato a nessun team"));

        Team result = registrationHandler.getTeamByUser(mockUser);

        assertNull(result);
        verify(registrationService, times(1)).getTeamByUser(mockUser);
    }
}