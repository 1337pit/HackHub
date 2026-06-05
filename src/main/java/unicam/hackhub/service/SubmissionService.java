package unicam.hackhub.service;

import unicam.hackhub.model.Submission;
import unicam.hackhub.model.Team;
import unicam.hackhub.repository.SubmissionRepository;
import unicam.hackhub.repository.TeamRepository;

import java.time.LocalDate;

public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final TeamRepository teamRepository;

    public SubmissionService(SubmissionRepository submissionRepository, TeamRepository teamRepository) {
        this.submissionRepository = submissionRepository;
        this.teamRepository = teamRepository;
    }

    /**
     * Aggiunge una submission ad un team.
     */
    public Submission uploadSubmission(Long teamID, Submission submission){
        if(teamID == null || submission == null){
            throw new IllegalArgumentException("Team ID and submission cannot be null");
        }

        Team team = teamRepository.findByID(teamID);

        if(team == null){
            throw new IllegalArgumentException("Team not found");
        }

        submission.setSubmissionOnDate(LocalDate.now());
        submissionRepository.save(submission);

        team.setSubmission(submission);
        teamRepository.save(team);

        return submission;
    }

    /**
     * Modifica una submission già esistente.
     */
    public Submission updateSubmission(Long submissionID, String name){
        if(submissionID == null || name == null || name.trim().isEmpty()){
            throw new IllegalArgumentException("Submission data cannot be null or empty");
        }

        Submission submission = submissionRepository.findByID(submissionID);

        if(submission == null){
            throw new IllegalArgumentException("Submission not found");
        }

        submission.setName(name);
        submission.setSubmissionOnDate(LocalDate.now());

        submissionRepository.save(submission);

        return submission;
    }

}
