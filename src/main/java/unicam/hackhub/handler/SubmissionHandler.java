package unicam.hackhub.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unicam.hackhub.dto.EditEvaluationRequest;
import unicam.hackhub.dto.EvaluateSubmissionRequest;
import unicam.hackhub.model.Evaluation;
import unicam.hackhub.model.Submission;
import unicam.hackhub.service.SubmissionService;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionHandler {

    private final SubmissionService submissionService;

    // Grazie all'iniezione da costruttore, submissionService non sarà MAI null.
    // Rimosso l'obbligo di fare controlli manuali anti-Null.
    public SubmissionHandler(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    /**
     * Carica una nuova sottomissione per un team.
     * POST /api/submissions/team/5
     */
    @PostMapping("/team/{teamId}")
    public ResponseEntity<Submission> uploadSubmission(
            @PathVariable Long teamId,
            @RequestBody Submission submission) {

        Submission created = submissionService.uploadSubmission(teamId, submission);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Aggiorna il nome di una sottomissione esistente.
     * PATCH /api/submissions/12/name?name=NuovoTitolo
     */
    @PatchMapping("/{submissionId}/name")
    public ResponseEntity<Submission> updateSubmission(
            @PathVariable Long submissionId,
            @RequestParam String name) {

        Submission updated = submissionService.updateSubmission(submissionId, name);
        return ResponseEntity.ok(updated);
    }

    /**
     * Elimina una sottomissione (Verificando utente e team).
     * DELETE /api/submissions/12?userId=3&teamId=5
     */
    @DeleteMapping("/{submissionId}")
    public ResponseEntity<Void> deleteSubmission(
            @PathVariable Long submissionId,
            @RequestParam Long userId,
            @RequestParam Long teamId) {

        // Rimosso try-catch: l'eccezione bolle al GlobalExceptionHandler
        submissionService.deleteSubmission(submissionId, userId, teamId);
        return ResponseEntity.noContent().build(); // Ritorna un pulito 204 No Content
    }

    /**
     * Inserisce la valutazione di un giudice per una sottomissione.
     * POST /api/submissions/12/evaluate
     */
    @PostMapping("/{submissionId}/evaluate")
    public ResponseEntity<Evaluation> evaluateSubmission(
            @PathVariable Long submissionId,
            @RequestBody EvaluateSubmissionRequest request) {

        Evaluation evaluation = submissionService.evaluateSubmission(
                request.judgeId(),
                submissionId,
                request.grade(),
                request.briefJudgment()
        );
        return new ResponseEntity<>(evaluation, HttpStatus.CREATED);
    }

    /**
     * Modifica una valutazione esistente.
     * PUT /api/submissions/12/evaluate
     */
    @PutMapping("/{submissionId}/evaluate")
    public ResponseEntity<Evaluation> editEvaluateSubmission(
            @PathVariable Long submissionId,
            @RequestBody EditEvaluationRequest request) {

        Evaluation updatedEvaluation = submissionService.editEvaluateSubmission(
                request.judgeId(),
                submissionId,
                request.hackathonId(),
                request.evaluationId(),
                request.grade(),
                request.briefJudgment()
        );
        return ResponseEntity.ok(updatedEvaluation);
    }
}