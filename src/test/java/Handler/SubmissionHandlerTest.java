package Handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unicam.hackhub.handler.SubmissionHandler;
import unicam.hackhub.model.Evaluation;
import unicam.hackhub.model.Submission;
import unicam.hackhub.service.SubmissionService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test unitari per SubmissionHandler.
 *
 * Verifica che l'handler:
 *  - deleghi correttamente a SubmissionService
 *  - lanci IllegalStateException se il service non è stato impostato
 *  - per evaluateSubmission, catturi le IllegalArgumentException restituendo null
 */
@ExtendWith(MockitoExtension.class)
class SubmissionHandlerTest {

    @Mock
    private SubmissionService submissionService;

    private SubmissionHandler submissionHandler;

    private Submission submission;

    @BeforeEach
    void setUp() {
        submissionHandler = new SubmissionHandler(submissionService);
        submission = new Submission(1L, "Initial Submission");
    }

    // -----------------------------------------------------------------------
    // uploadSubmission
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("uploadSubmission – delega al service e restituisce la submission")
    void uploadSubmission_delegatesToService_returnsSubmission() {
        when(submissionService.uploadSubmission(1L, submission)).thenReturn(submission);

        Submission result = submissionHandler.uploadSubmission(1L, submission);

        assertNotNull(result);
        assertEquals("Initial Submission", result.getName());
        verify(submissionService).uploadSubmission(1L, submission);
    }

    @Test
    @DisplayName("uploadSubmission – service non impostato → IllegalStateException")
    void uploadSubmission_serviceNotSet_throwsException() {
        SubmissionHandler handlerWithoutService = new SubmissionHandler();

        assertThrows(IllegalStateException.class,
                () -> handlerWithoutService.uploadSubmission(1L, submission));
    }

    @Test
    @DisplayName("uploadSubmission – il service lancia eccezione → l'handler la propaga")
    void uploadSubmission_serviceThrows_propagatesException() {
        when(submissionService.uploadSubmission(anyLong(), any()))
                .thenThrow(new IllegalArgumentException("Team not found"));

        assertThrows(IllegalArgumentException.class,
                () -> submissionHandler.uploadSubmission(1L, submission));
    }

    // -----------------------------------------------------------------------
    // updateSubmission
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("updateSubmission – delega al service e restituisce la submission")
    void updateSubmission_delegatesToService_returnsSubmission() {
        when(submissionService.updateSubmission(1L, "Updated")).thenReturn(submission);

        Submission result = submissionHandler.updateSubmission(1L, "Updated");

        assertNotNull(result);
        verify(submissionService).updateSubmission(1L, "Updated");
    }

    @Test
    @DisplayName("updateSubmission – service non impostato → IllegalStateException")
    void updateSubmission_serviceNotSet_throwsException() {
        SubmissionHandler handlerWithoutService = new SubmissionHandler();

        assertThrows(IllegalStateException.class,
                () -> handlerWithoutService.updateSubmission(1L, "Updated"));
    }

    // -----------------------------------------------------------------------
    // evaluateSubmission - Happy path
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("evaluateSubmission – valutazione riuscita → restituisce l'Evaluation")
    void evaluateSubmission_success_returnsEvaluation() {
        Evaluation expectedEvaluation = new Evaluation("Great work", 8);
        when(submissionService.evaluateSubmission(1L, 1L, 8, "Great work"))
                .thenReturn(expectedEvaluation);

        Evaluation result = submissionHandler.evaluateSubmission(1L, 1L, 8, "Great work");

        assertNotNull(result);
        assertEquals(8, result.getGrade());
        verify(submissionService, times(1)).evaluateSubmission(1L, 1L, 8, "Great work");
    }

    // -----------------------------------------------------------------------
    // evaluateSubmission - Gestione errori: l'handler cattura IllegalArgumentException
    // e restituisce null
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("evaluateSubmission – judge non trovato → handler restituisce null")
    void evaluateSubmission_judgeNotFound_returnsNull() {
        when(submissionService.evaluateSubmission(anyLong(), anyLong(), anyInt(), any()))
                .thenThrow(new IllegalArgumentException("Judge not found"));

        Evaluation result = submissionHandler.evaluateSubmission(99L, 1L, 8, "Great work");

        assertNull(result);
    }

    @Test
    @DisplayName("evaluateSubmission – submission già valutata → handler restituisce null")
    void evaluateSubmission_alreadyEvaluated_returnsNull() {
        when(submissionService.evaluateSubmission(anyLong(), anyLong(), anyInt(), any()))
                .thenThrow(new IllegalArgumentException("Submission already evaluated"));

        Evaluation result = submissionHandler.evaluateSubmission(1L, 1L, 8, "Great work");

        assertNull(result);
    }

    @Test
    @DisplayName("evaluateSubmission – service non impostato → IllegalStateException")
    void evaluateSubmission_serviceNotSet_throwsException() {
        SubmissionHandler handlerWithoutService = new SubmissionHandler();

        assertThrows(IllegalStateException.class,
                () -> handlerWithoutService.evaluateSubmission(1L, 1L, 8, "Great work"));
    }

    // -----------------------------------------------------------------------
    // evaluateSubmission - Delega: l'handler non fa logica, la lascia tutta al service
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("evaluateSubmission – i parametri vengono passati intatti al service")
    void evaluateSubmission_parametersPassedThrough() {
        Evaluation expectedEvaluation = new Evaluation("Great work", 8);
        when(submissionService.evaluateSubmission(1L, 1L, 8, "Great work"))
                .thenReturn(expectedEvaluation);

        submissionHandler.evaluateSubmission(1L, 1L, 8, "Great work");

        verify(submissionService).evaluateSubmission(1L, 1L, 8, "Great work");
    }
}