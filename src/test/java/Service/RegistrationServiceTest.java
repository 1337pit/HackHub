package Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock private RegistrationRepository registrationRepository;
    @Mock private TeamRepository teamRepository;
    @Mock private HackathonRepository hackathonRepository;
    @Mock private UserRepository userRepository;
    @Mock private UserService userService;
    @Mock private HackathonService hackathonService;

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

    // =========================================================================
    // 1. Test per registerTeam
    // =========================================================================

    @Test
    @DisplayName("registerTeam – Condizioni valide → Salva e restituisce la registrazione")
    void registerTeam_Success() {
        Long hackathonId = 1L;
        Long userId = 2L;

        // Stubbing per il recupero dati
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.hasTeam()).thenReturn(true);
        when(mockUser.getCurrentTeam()).thenReturn(mockTeam);
        when(hackathonRepository.findById(hackathonId)).thenReturn(Optional.of(mockHackathon));

        // Nessuna registrazione precedente trovata (ritorna Optional.empty())
        when(registrationRepository.findByTeamAndHackathon(mockTeam, mockHackathon)).thenReturn(Optional.empty());

        // Risposta al salvataggio finale
        when(registrationRepository.save(any(Registration.class))).thenReturn(mockRegistration);

        Registration result = registrationService.registerTeam(hackathonId, userId);

        assertNotNull(result);
        assertEquals(mockRegistration, result);

        // Verifiche delle interazioni ed effetti collaterali richiesti dalla logica di business
        verify(hackathonService).checkHackathonAvailability(mockHackathon);
        verify(hackathonService).checkTeamSize(mockTeam, mockHackathon);
        verify(hackathonService).checkTeamAlreadyRegistered(null);
        verify(mockTeam).setHackathon(mockHackathon);
        verify(teamRepository).save(mockTeam);
        verify(registrationRepository).save(any(Registration.class));
    }

    @Test
    @DisplayName("registerTeam – Parametri nulli → Lancia IllegalArgumentException")
    void registerTeam_NullParameters_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> registrationService.registerTeam(null, 1L));
        assertThrows(IllegalArgumentException.class, () -> registrationService.registerTeam(1L, null));
    }

    @Test
    @DisplayName("registerTeam – L'utente non possiede un team → Si interrompe lanciando eccezione")
    void registerTeam_UserHasNoTeam_ThrowsException() {
        Long hackathonId = 1L;
        Long userId = 2L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.hasTeam()).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                registrationService.registerTeam(hackathonId, userId)
        );

        assertEquals("User has no team", exception.getMessage());

        // Verifica il corto circuito: non deve cercare l'hackathon né salvare nulla
        verify(hackathonRepository, never()).findById(any());
        verify(registrationRepository, never()).save(any());
    }

    // =========================================================================
    // 2. Test per getRegistration
    // =========================================================================

    @Test
    @DisplayName("getRegistration – TeamID valido → Restituisce la registrazione associata")
    void getRegistration_Success() {
        Long teamId = 10L;
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(mockTeam));
        when(registrationRepository.findByTeam(mockTeam)).thenReturn(Optional.of(mockRegistration));

        Registration result = registrationService.getRegistration(teamId);

        assertNotNull(result);
        assertEquals(mockRegistration, result);
        verify(teamRepository).findById(teamId);
        verify(registrationRepository).findByTeam(mockTeam);
    }

    @Test
    @DisplayName("getRegistration – TeamID nullo → Lancia IllegalArgumentException")
    void getRegistration_NullTeamId_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> registrationService.getRegistration(null));
    }

    // =========================================================================
    // 3. Test per getTeamByUser
    // =========================================================================

    @Test
    @DisplayName("getTeamByUser – Utente valido → Estrae il team corretto dal repository")
    void getTeamByUser_Success() {
        when(teamRepository.findByUser(mockUser)).thenReturn(Optional.of(mockTeam));

        Team result = registrationService.getTeamByUser(mockUser);

        assertNotNull(result);
        assertEquals(mockTeam, result);
        verify(teamRepository).findByUser(mockUser);
    }
}