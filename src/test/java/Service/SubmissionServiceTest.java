package Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unicam.hackhub.model.*;
import unicam.hackhub.repository.*;
import unicam.hackhub.service.SubmissionService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {

    @Mock private SubmissionRepository submissionRepository;
    @Mock private TeamRepository teamRepository;
    @Mock private StaffMemberRepository staffMemberRepository;
    @Mock private HackathonRepository hackathonRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private SubmissionService submissionService;

    private Team mockTeam;
    private Submission mockSubmission;
    private User mockUser;
    private Judge mockJudge;
    private Evaluation mockEvaluation;
    private Hackathon mockHackathon;

    @BeforeEach
    void setUp() {
        mockTeam = mock(Team.class);
        mockSubmission = mock(Submission.class);
        mockUser = mock(User.class);
        mockJudge = mock(Judge.class);
        mockEvaluation = mock(Evaluation.class);
        mockHackathon = mock(Hackathon.class);
    }

    // =========================================================================
    // 1. Test per uploadSubmission
    // =========================================================================

    @Test
    @DisplayName("uploadSubmission – Team valido senza sottomissioni → Salva con successo")
    void uploadSubmission_Success() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(mockTeam));
        when(mockTeam.getSubmission()).thenReturn(null); // Nessuna sottomissione precedente
        when(submissionRepository.save(mockSubmission)).thenReturn(mockSubmission);
        when(teamRepository.save(mockTeam)).thenReturn(mockTeam);

        Submission result = submissionService.uploadSubmission(1L, mockSubmission);

        assertNotNull(result);
        verify(mockSubmission).setSubmissionOnDate(any());
        verify(mockTeam).setSubmission(mockSubmission);
        verify(submissionRepository).save(mockSubmission);
        verify(teamRepository).save(mockTeam);
    }

    @Test
    @DisplayName("uploadSubmission – Team ha già caricato una sottomissione → Lancia IllegalArgumentException")
    void uploadSubmission_AlreadyExists_ThrowsException() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(mockTeam));
        when(mockTeam.getSubmission()).thenReturn(mockSubmission); // Già presente

        assertThrows(IllegalArgumentException.class, () -> submissionService.uploadSubmission(1L, mockSubmission));
        verify(submissionRepository, never()).save(any());
    }

    // =========================================================================
    // 2. Test per updateSubmission
    // =========================================================================

    @Test
    @DisplayName("updateSubmission – Sottomissione esistente e testo valido → Aggiorna con successo")
    void updateSubmission_Success() {
        when(submissionRepository.findById(1L)).thenReturn(Optional.of(mockSubmission));
        when(submissionRepository.save(mockSubmission)).thenReturn(mockSubmission);

        Submission result = submissionService.updateSubmission(1L, "New Name");

        assertNotNull(result);
        verify(mockSubmission).setName("New Name");
        verify(mockSubmission).setSubmissionOnDate(any());
        verify(submissionRepository).save(mockSubmission);
    }

    // =========================================================================
    // 3. Test per deleteSubmission
    // =========================================================================

    @Test
    @DisplayName("deleteSubmission – Utente e team validi proprietari del progetto → Elimina con successo")
    void deleteSubmission_Success() {
        when(submissionRepository.findById(10L)).thenReturn(Optional.of(mockSubmission));
        when(teamRepository.findById(20L)).thenReturn(Optional.of(mockTeam));
        when(userRepository.findById(30L)).thenReturn(Optional.of(mockUser));

        when(mockUser.getCurrentTeam()).thenReturn(mockTeam);
        when(mockTeam.getId()).thenReturn(20L);
        when(mockTeam.getSubmission()).thenReturn(mockSubmission);
        when(mockSubmission.getId()).thenReturn(10L);

        assertDoesNotThrow(() -> submissionService.deleteSubmission(10L, 30L, 20L));
        verify(submissionRepository).delete(mockSubmission);
    }

    @Test
    @DisplayName("deleteSubmission – Utente non appartenente a questo team → Lancia eccezione")
    void deleteSubmission_UserNotInTeam_ThrowsException() {
        when(submissionRepository.findById(10L)).thenReturn(Optional.of(mockSubmission));
        when(teamRepository.findById(20L)).thenReturn(Optional.of(mockTeam));
        when(userRepository.findById(30L)).thenReturn(Optional.of(mockUser));

        Team maliciousTeam = mock(Team.class);
        when(maliciousTeam.getId()).thenReturn(999L);
        when(mockUser.getCurrentTeam()).thenReturn(maliciousTeam);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> submissionService.deleteSubmission(10L, 30L, 20L));

        // FIX: Cambiato da "User not in this team" a "User is not in this team"
        assertEquals("User is not in this team", ex.getMessage());
        verify(submissionRepository, never()).delete(any());
    }

    // =========================================================================
    // 4. Test per evaluateSubmission
    // =========================================================================

    @Test
    @DisplayName("evaluateSubmission – Giudice e sottomissione validi → Ritorna la valutazione")
    void evaluateSubmission_Success() {
        // CORRETTO: Mocking dei soli comportamenti dei Repository e dei modelli di dati
        when(staffMemberRepository.findById(1L)).thenReturn(Optional.of(mockJudge));
        when(submissionRepository.findById(2L)).thenReturn(Optional.of(mockSubmission));
        when(mockSubmission.getGrade()).thenReturn(null); // Non ancora valutata

        Evaluation result = submissionService.evaluateSubmission(1L, 2L, 8, "Great work");

        assertNotNull(result);
        assertEquals(8, result.getGrade());
        // Se la tua classe Evaluation mappa la stringa su briefJudgment o briefJudgement verifica la corrispondenza
        verify(submissionRepository).save(mockSubmission);
    }

    // =========================================================================
    // 5. Test per editEvaluateSubmission
    // =========================================================================

    @Test
    @DisplayName("editEvaluateSubmission – Modifica corretta di una valutazione da parte del giudice")
    void editEvaluateSubmission_Success() {
        when(staffMemberRepository.findById(1L)).thenReturn(Optional.of(mockJudge));
        when(hackathonRepository.findById(5L)).thenReturn(Optional.of(mockHackathon));
        when(submissionRepository.findById(2L)).thenReturn(Optional.of(mockSubmission));

        when(mockSubmission.getGrade()).thenReturn(mockEvaluation);
        when(mockEvaluation.getId()).thenReturn(100L);

        // CORRETTO: Rimossa la chiamata fittizia a when(submissionService...)
        Evaluation result = submissionService.editEvaluateSubmission(1L, 2L, 5L, 100L, 9, "Updated text");

        assertNotNull(result);
        verify(mockEvaluation).setGrade(9);
        verify(mockEvaluation).setBriefJudgement("Updated text");
        verify(submissionRepository).save(mockSubmission);
    }
}