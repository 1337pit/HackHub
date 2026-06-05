package unicam.hackhub.handler;

import unicam.hackhub.model.Submission;
import unicam.hackhub.service.SubmissionService;

public class SubmissionHandler {

    private SubmissionService submissionService;

    public SubmissionHandler() {

    }

    public SubmissionHandler(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    public Submission uploadSubmission(Long teamID, Submission submission) {
        if(submissionService == null){
            throw new IllegalStateException("Submission service is not defined");
        }

        return submissionService.uploadSubmission(teamID, submission);
    }

    public Submission updateSubmission(Long submissionID, String name) {
        if(submissionService == null){
            throw new IllegalStateException("Submission service is not defined");
        }

        return submissionService.updateSubmission(submissionID, name);
    }

}