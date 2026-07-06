package Handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import unicam.hackhub.dto.EditEvaluationRequest;
import unicam.hackhub.dto.EvaluateSubmissionRequest;
import unicam.hackhub.handler.SubmissionHandler;
import unicam.hackhub.model.Evaluation;
import unicam.hackhub.model.Submission;
import unicam.hackhub.service.SubmissionService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SubmissionHandlerTest {

    private SubmissionService submissionService;
    private SubmissionHandler submissionHandler;
    private Submission mockSubmission;
    private Evaluation mockEvaluation;

    @BeforeEach
    void setUp() {
        // Mock istantaneo del servizio ed iniezione diretta nel costruttore
        submissionService = Mockito.mock(SubmissionService.class);
        submissionHandler = new SubmissionHandler(submissionService);

        mockSubmission = Mockito.mock(Submission.class);
        mockEvaluation = Mockito.mock(Evaluation.class);
    }

    // -----------------------------------------------------------------------
    // uploadSubmission (POST)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("uploadSubmission – Successo restituisce 201 Created")
    void uploadSubmission_Success() {
        when(submissionService.uploadSubmission(eq(1L), any(Submission.class))).thenReturn(mockSubmission);

        ResponseEntity<Submission> response = submissionHandler.uploadSubmission(1L, mockSubmission);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockSubmission, response.getBody());
        verify(submissionService).uploadSubmission(1L, mockSubmission);
    }

    // -----------------------------------------------------------------------
    // updateSubmission (PATCH)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("updateSubmission – Successo restituisce 200 OK")
    void updateSubmission_Success() {
        when(submissionService.updateSubmission(1L, "NuovoTitolo")).thenReturn(mockSubmission);

        ResponseEntity<Submission> response = submissionHandler.updateSubmission(1L, "NuovoTitolo");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockSubmission, response.getBody());
        verify(submissionService).updateSubmission(1L, "NuovoTitolo");
    }

    // -----------------------------------------------------------------------
    // deleteSubmission (DELETE)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("deleteSubmission – Successo restituisce 204 No Content")
    void deleteSubmission_Success() {
        // Metodo void nel service, facciamo solo il verify
        ResponseEntity<Void> response = submissionHandler.deleteSubmission(12L, 3L, 5L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(submissionService).deleteSubmission(12L, 3L, 5L);
    }

    // -----------------------------------------------------------------------
    // evaluateSubmission (POST)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("evaluateSubmission – Successo con DTO restituisce 201 Created")
    void evaluateSubmission_Success() {
        EvaluateSubmissionRequest request = new EvaluateSubmissionRequest(2L, 8, "Ottimo lavoro");
        when(submissionService.evaluateSubmission(2L, 12L, 8, "Ottimo lavoro")).thenReturn(mockEvaluation);

        ResponseEntity<Evaluation> response = submissionHandler.evaluateSubmission(12L, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockEvaluation, response.getBody());
        verify(submissionService).evaluateSubmission(2L, 12L, 8, "Ottimo lavoro");
    }

    // -----------------------------------------------------------------------
    // editEvaluateSubmission (PUT)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("editEvaluateSubmission – Successo con DTO restituisce 200 OK")
    void editEvaluateSubmission_Success() {
        EditEvaluationRequest request = new EditEvaluationRequest(2L, 100L, 50L, 9, "Giudizio modificato");
        when(submissionService.editEvaluateSubmission(2L, 12L, 100L, 50L, 9, "Giudizio modificato"))
                .thenReturn(mockEvaluation);

        ResponseEntity<Evaluation> response = submissionHandler.editEvaluateSubmission(12L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockEvaluation, response.getBody());
        verify(submissionService).editEvaluateSubmission(2L, 12L, 100L, 50L, 9, "Giudizio modificato");
    }
}