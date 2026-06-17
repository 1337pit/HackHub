package unicam.hackhub.handler;

import unicam.hackhub.model.Evaluation;
import unicam.hackhub.model.Submission;
import unicam.hackhub.service.SubmissionService;

public class SubmissionHandler {

    private SubmissionService submissionService;

    public SubmissionHandler() {}

    public SubmissionHandler(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    public Submission uploadSubmission(Long teamID, Submission submission) {
        if (submissionService == null)
            throw new IllegalStateException("Submission service is not defined");
        return submissionService.uploadSubmission(teamID, submission);
    }

    public Submission updateSubmission(Long submissionID, String name) {
        if (submissionService == null)
            throw new IllegalStateException("Submission service is not defined");
        return submissionService.updateSubmission(submissionID, name);
    }

    /**
     * Gestisce la richiesta di valutazione di una sottomissione.
     */
    public Evaluation evaluateSubmission(Long judgeID, Long submissionID, int grade, String briefJudgment) {
        if (submissionService == null)
            throw new IllegalStateException("Submission service is not defined");
        try {
            return submissionService.evaluateSubmission(judgeID, submissionID, grade, briefJudgment);
        } catch (IllegalArgumentException e) {
            System.err.println("evaluateSubmission error: " + e.getMessage());
            return null;
        }
    }
}