package unicam.hackhub.service;

import org.springframework.stereotype.Service;
import unicam.hackhub.model.*;
import unicam.hackhub.repository.*;

import java.time.LocalDate;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final TeamRepository teamRepository;
    private final StaffMemberRepository staffMemberRepository;
    private final UserRepository userRepository;
    private final HackathonRepository hackathonRepository;

    public SubmissionService(SubmissionRepository submissionRepository,
                             TeamRepository teamRepository,
                             StaffMemberRepository staffMemberRepository,
                             UserRepository userRepository,
                             HackathonRepository hackathonRepository) {
        this.submissionRepository = submissionRepository;
        this.teamRepository = teamRepository;
        this.staffMemberRepository = staffMemberRepository;
        this.userRepository = userRepository;
        this.hackathonRepository = hackathonRepository;
    }

    /**
     * Aggiunge una submission a un team.
     */
    public Submission uploadSubmission(Long teamID, Submission submission) {
        if (teamID == null || submission == null)
            throw new IllegalArgumentException("Team ID and submission cannot be null");

        Team team = teamRepository.findById(teamID)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));

        if (team.getSubmission() != null)
            throw new IllegalArgumentException("Submission already exists for this team");

        submission.setSubmissionOnDate(LocalDate.now());
        submissionRepository.save(submission);

        team.setSubmission(submission);
        teamRepository.save(team);

        return submission;
    }

    /**
     * Modifica una submission già esistente.
     */
    public Submission updateSubmission(Long submissionID, String name) {
        if (submissionID == null || name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Submission data cannot be null or empty");

        Submission submission = submissionRepository.findById(submissionID)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found"));

        submission.setName(name);
        submission.setSubmissionOnDate(LocalDate.now());
        return submissionRepository.save(submission);
    }

    /**
     * Elimina una sottomissione.
     * 1. Verifica dati e recupera entità
     * 2. Verifica che l'utente faccia parte del team
     * 3. Verifica che la submission appartenga al team
     * 4. Elimina la submission
     */
    public void deleteSubmission(Long submissionID, Long userID, Long teamID) {
        if (submissionID == null || userID == null || teamID == null)
            throw new IllegalArgumentException("submissionID, userID and teamID cannot be null");

        Submission submission = submissionRepository.findById(submissionID)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found"));

        teamRepository.findById(teamID)
                .orElseThrow(() -> new IllegalArgumentException("Team does not exist"));

        User user = userRepository.findById(userID)
                .orElseThrow(() -> new IllegalArgumentException("User does not exist"));

        Team teamUser = user.getCurrentTeam();
        if (teamUser == null)
            throw new IllegalArgumentException("User is not in a team");
        if (!teamUser.getId().equals(teamID))
            throw new IllegalArgumentException("User is not in this team");

        Submission submissionToDelete = teamUser.getSubmission();
        if (submissionToDelete == null)
            throw new IllegalArgumentException("Submission not found");
        if (!submissionToDelete.getId().equals(submissionID))
            throw new IllegalArgumentException("Submission is not in the team");

        submissionRepository.delete(submission);
    }

    /**
     * Valuta una sottomissione.
     * 1. Recupera il giudice
     * 2. Recupera la submission e verifica che non sia già valutata
     * 3. Delega la valutazione a Judge.evaluateSubmission()
     * 4. Salva la submission aggiornata
     */
    public Evaluation evaluateSubmission(Long judgeID, Long submissionID,
                                         int grade, String briefJudgment) {
        if (judgeID == null || submissionID == null
                || briefJudgment == null || briefJudgment.trim().isEmpty())
            throw new IllegalArgumentException("Invalid data");

        Judge judge = (Judge) staffMemberRepository.findById(judgeID)
                .filter(s -> s instanceof Judge)
                .orElseThrow(() -> new IllegalArgumentException("Judge not found"));

        Submission submission = submissionRepository.findById(submissionID)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found"));

        if (submission.getGrade() != null)
            throw new IllegalArgumentException("Submission already evaluated");

        Evaluation evaluation = new Evaluation(briefJudgment, grade);
        submission.setGrade(evaluation);
        submissionRepository.save(submission);
        return evaluation;
    }

    /**
     * Modifica la valutazione di una sottomissione.
     * 1. Recupera il giudice e l'hackathon
     * 2. Recupera valutazione e submission
     * 3. Delega la modifica a Judge.editEvaluateSubmission()
     * 4. Salva la submission aggiornata
     */
    public Evaluation editEvaluateSubmission(Long judgeID, Long submissionID,
                                             Long hackathonID, Long evaluationID,
                                             int grade, String briefJudgment) {
        if (judgeID == null || submissionID == null || evaluationID == null
                || grade < 0 || grade > 10
                || briefJudgment == null || briefJudgment.trim().isEmpty())
            throw new IllegalArgumentException("Invalid data");

        Judge judge = (Judge) staffMemberRepository.findById(judgeID)
                .filter(s -> s instanceof Judge)
                .orElseThrow(() -> new IllegalArgumentException("Judge not found"));

        hackathonRepository.findById(hackathonID)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon not found"));

        Submission submission = submissionRepository.findById(submissionID)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found"));

        Evaluation evaluation = submission.getGrade();
        if (evaluation == null || !evaluation.getId().equals(evaluationID))
            throw new IllegalArgumentException("Evaluation not found");

        evaluation.setGrade(grade);
        evaluation.setBriefJudgement(briefJudgment);
        submission.setGrade(evaluation);
        submissionRepository.save(submission);

        return evaluation;
    }
}