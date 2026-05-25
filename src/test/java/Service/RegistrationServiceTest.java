package Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unicam.hackhub.model.Hackathon;
import unicam.hackhub.model.Registration;
import unicam.hackhub.model.Team;
import unicam.hackhub.model.User;
import unicam.hackhub.repository.HackathonRepository;
import unicam.hackhub.repository.RegistrationRepository;
import unicam.hackhub.repository.TeamRepository;
import unicam.hackhub.repository.UserRepository;
import unicam.hackhub.service.HackathonService;
import unicam.hackhub.service.RegistrationService;
import unicam.hackhub.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTest {

    @Mock
    private RegistrationRepository registrationRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private HackathonRepository hackathonRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private HackathonService hackathonService;

    @InjectMocks
    private RegistrationService registrationService;

    private User mockUser;
    private Team mockTeam;
    private Hackathon mockHackathon;
    private Registration mockRegistration;

    @BeforeEach
    void setUp() {
        mockUser = mock(User.class);
        mockTeam = mock(Team.class);
        mockHackathon = mock(Hackathon.class);
        mockRegistration = mock(Registration.class);
    }

    // --- TEST PER registerTeam ---

    @Test
    void registerTeam_ShouldReturnRegistration_WhenAllConditionsAreValid() {
        // Arrange
        Long hackathonId = 1L;
        Long userId = 2L;

        when(userRepository.findByID(userId)).thenReturn(mockUser);
        when(mockUser.hasTeam()).thenReturn(true);
        when(mockUser.getCurrentTeam()).thenReturn(mockTeam);
        when(hackathonRepository.findByID(hackathonId)).thenReturn(mockHackathon);
        when(registrationRepository.findByRegistration(mockTeam, mockHackathon)).thenReturn(mockRegistration);

        // Act
        Registration result = registrationService.registerTeam(hackathonId, userId);

        // Assert
        assertNotNull(result);
        assertEquals(mockRegistration, result);

        // Verifica che le validazioni dei servizi esterni vengano effettivamente chiamate
        verify(userService).checkEligibility(mockUser);
        verify(hackathonService).checkHackathonAvailability(mockHackathon);
        verify(hackathonService).checkTeamSize(mockTeam, mockHackathon);
        verify(hackathonService).checkTeamAlreadyRegistered(mockRegistration);
        verify(registrationRepository).save(mockRegistration);
    }

    @Test
    void registerTeam_ShouldThrowIllegalArgumentException_WhenParametersAreNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> registrationService.registerTeam(null, 1L));
        assertThrows(IllegalArgumentException.class, () -> registrationService.registerTeam(1L, null));
    }

    @Test
    void registerTeam_ShouldThrowIllegalArgumentException_WhenUserHasNoTeam() {
        // Arrange
        Long hackathonId = 1L;
        Long userId = 2L;

        when(userRepository.findByID(userId)).thenReturn(mockUser);
        when(mockUser.hasTeam()).thenReturn(false); // L'utente non ha un team

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.registerTeam(hackathonId, userId);
        });

        assertEquals("User has no team", exception.getMessage());

        // Verifica che il flusso si interrompa prima di cercare l'hackathon o salvare
        verify(hackathonRepository, never()).findByID(any());
        verify(registrationRepository, never()).save(any());
    }

    // --- TEST PER getRegistration ---

    @Test
    void getRegistration_ShouldReturnRegistration_WhenTeamIdIsValid() {
        // Arrange
        Long teamId = 10L;
        when(registrationRepository.findByTeamID(teamId)).thenReturn(mockRegistration);

        // Act
        Registration result = registrationService.getRegistration(teamId);

        // Assert
        assertNotNull(result);
        assertEquals(mockRegistration, result);
        verify(registrationRepository).findByTeamID(teamId);
    }

    @Test
    void getRegistration_ShouldThrowIllegalArgumentException_WhenTeamIdIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> registrationService.getRegistration(null));
    }

    // --- TEST PER getTeamByUser ---

    @Test
    void getTeamByUser_ShouldReturnTeam_WhenUserIsValid() {
        // Arrange
        when(teamRepository.findByUser(mockUser)).thenReturn(mockTeam);

        // Act
        Team result = registrationService.getTeamByUser(mockUser);

        // Assert
        assertNotNull(result);
        assertEquals(mockTeam, result);
        verify(teamRepository).findByUser(mockUser);
    }
}