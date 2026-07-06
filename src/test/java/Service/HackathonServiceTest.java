package Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unicam.hackhub.model.*;
import unicam.hackhub.model.state.RegistrationState;
import unicam.hackhub.repository.*;
import unicam.hackhub.service.HackathonService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HackathonServiceTest {

    @Mock private HackathonRepository hackathonRepository;
    @Mock private StaffMemberRepository staffMemberRepository;
    @Mock private UserRepository userRepository;
    @Mock private TeamRepository teamRepository;
    @Mock private RegistrationRepository registrationRepository;
    @Mock private SupportRepository supportRepository;

    @InjectMocks
    private HackathonService hackathonService;

    private Organizer mockOrganizer;
    private Mentor mockMentor;
    private Judge mockJudge;
    private Hackathon mockHackathon;

    @BeforeEach
    void setUp() {
        mockOrganizer = mock(Organizer.class);
        mockMentor = mock(Mentor.class);
        mockJudge = mock(Judge.class);
        mockHackathon = mock(Hackathon.class);
    }

    // =========================================================================
    // 1. Test per createHackathon
    // =========================================================================

    @Test
    @DisplayName("createHackathon – Flusso di successo")
    void createHackathon_Success() {
        when(hackathonRepository.findByNameHackathon("HackHub Test")).thenReturn(Optional.empty());
        when(staffMemberRepository.findById(2L)).thenReturn(Optional.of(mockMentor));
        when(staffMemberRepository.findById(3L)).thenReturn(Optional.of(mockJudge));
        when(hackathonRepository.save(any(Hackathon.class))).thenReturn(mockHackathon);
        when(mockHackathon.getNameHackathon()).thenReturn("HackHub Test");

        Hackathon created = hackathonService.createHackathon(
                "HackHub Test", "Rulebook", LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(12),
                "Camerino", "1000", new RegistrationState(), 5, mockOrganizer, 2L, 3L
        );

        assertNotNull(created);
        assertEquals("HackHub Test", created.getNameHackathon());
        verify(hackathonRepository).save(any(Hackathon.class));
    }

    @Test
    @DisplayName("createHackathon – Nome duplicato lancia IllegalArgumentException")
    void createHackathon_DuplicateName_ThrowsException() {
        when(hackathonRepository.findByNameHackathon("HackHub Test")).thenReturn(Optional.of(mockHackathon));

        assertThrows(IllegalArgumentException.class, () -> hackathonService.createHackathon(
                "HackHub Test", "Rulebook", LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(12),
                "Camerino", "1000", new RegistrationState(), 5, mockOrganizer, 2L, 3L
        ));
    }

    // =========================================================================
    // 2. Test per editHackathon
    // =========================================================================

    @Test
    @DisplayName("editHackathon – Modifica con successo")
    void editHackathon_Success() {
        when(hackathonRepository.findById(1L)).thenReturn(Optional.of(mockHackathon));
        when(mockHackathon.isRegistrationOpen()).thenReturn(true);
        when(hackathonRepository.save(mockHackathon)).thenReturn(mockHackathon);

        Hackathon updated = hackathonService.editHackathon(
                1L, "New Name", "New Rules", LocalDate.now().plusDays(1),
                "New Loc", "New Prize", 10, null, null
        );

        assertNotNull(updated);
        verify(mockHackathon).setNameHackathon("New Name");
        verify(hackathonRepository).save(mockHackathon);
    }

    // =========================================================================
    // 3. Test per addMentor
    // =========================================================================

    @Test
    @DisplayName("addMentor – Aggiunta mentore con successo")
    void addMentor_Success() {
        // 1. Il servizio recupera l'hackathon a cui associare il mentore
        when(hackathonRepository.findById(1L)).thenReturn(Optional.of(mockHackathon));

        // 2. Il servizio cerca l'utente tramite email a sistema (FIX)
        User mockUser = mock(User.class);
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(mockUser));

        // 3. Il servizio controlla che non sia già registrato nello staff con quella email
        when(staffMemberRepository.findByEmail("john@test.com")).thenReturn(Optional.empty());

        // Esecuzione del test
        Mentor added = hackathonService.addMentor("john@test.com", 1L);

        // Asserzioni
        assertNotNull(added);
        verify(mockHackathon).addMentor(any(Mentor.class));
    }

    // =========================================================================
    // 4. Test per getAssignedHackathons
    // =========================================================================

    @Test
    @DisplayName("getAssignedHackathons – Ritorna la lista dal repository custom")
    void getAssignedHackathons_Success() {
        when(staffMemberRepository.findById(10L)).thenReturn(Optional.of(mockMentor));
        when(hackathonRepository.findByStaffMemberId(10L)).thenReturn(List.of(mockHackathon));

        List<Hackathon> result = hackathonService.getAssignedHackathons(10L);

        assertEquals(1, result.size());
        assertEquals(mockHackathon, result.get(0));
        verify(hackathonRepository).findByStaffMemberId(10L);
    }

    // =========================================================================
    // 5. Test per getRequestsSupport
    // =========================================================================

    @Test
    @DisplayName("getRequestsSupport – Estrae le richieste per il mentore assegnato")
    void getRequestsSupport_Success() {
        // Fix per checkAssignedHackathon interno
        when(staffMemberRepository.findById(20L)).thenReturn(Optional.of(mockMentor));
        when(hackathonRepository.findByStaffMemberId(20L)).thenReturn(List.of(mockHackathon));
        when(mockHackathon.getId()).thenReturn(1L);
        when(hackathonRepository.findById(1L)).thenReturn(Optional.of(mockHackathon));

        SupportRequest mockRequest = mock(SupportRequest.class);
        when(supportRepository.findByMentor(mockMentor)).thenReturn(List.of(mockRequest));

        List<SupportRequest> results = hackathonService.getRequestsSupport(20L, 1L);

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
    }

    // =========================================================================
    // 6. Test per getSubmissions
    // =========================================================================

    @Test
    @DisplayName("getSubmissions – Estrae correttamente gli elaborati dai team")
    void getSubmissions_Success() {
        // FIX: Aggiunto lo stubbing mancante per staffMemberRepository per evitare l'IllegalArgumentException
        when(staffMemberRepository.findById(30L)).thenReturn(Optional.of(mockMentor));
        when(hackathonRepository.findByStaffMemberId(30L)).thenReturn(List.of(mockHackathon));
        when(mockHackathon.getId()).thenReturn(1L);
        when(hackathonRepository.findById(1L)).thenReturn(Optional.of(mockHackathon));

        Registration mockRegistration = mock(Registration.class);
        Team mockTeam = mock(Team.class);
        Submission mockSubmission = mock(Submission.class);

        when(mockRegistration.getTeam()).thenReturn(mockTeam);
        when(mockTeam.getSubmission()).thenReturn(mockSubmission);
        when(registrationRepository.findByHackathon(mockHackathon)).thenReturn(List.of(mockRegistration));

        List<Submission> result = hackathonService.getSubmissions(30L, 1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(mockSubmission, result.get(0));
    }
}