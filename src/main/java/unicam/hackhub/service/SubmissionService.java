package unicam.hackhub.service;

import unicam.hackhub.model.*;
import unicam.hackhub.repository.HackathonRepository;
import unicam.hackhub.repository.StaffMemberRepository;
import unicam.hackhub.repository.SubmissionRepository;
import unicam.hackhub.repository.TeamRepository;

import java.time.LocalDate;

public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final TeamRepository teamRepository;
    private final StaffMemberRepository staffMemberRepository;
    private final HackathonRepository hackathonRepository;

    public SubmissionService(SubmissionRepository submissionRepository,
                             TeamRepository teamRepository,
                             StaffMemberRepository staffMemberRepository,
                             HackathonRepository hackathonRepository) {
        this.submissionRepository = submissionRepository;
        this.teamRepository = teamRepository;
        this.staffMemberRepository = staffMemberRepository;
        this.hackathonRepository = hackathonRepository;
    }

    /**
     * Aggiunge una submission ad un team.
     */
    public Submission uploadSubmission(Long teamID, Submission submission) {
        if (teamID == null || submission == null)
            throw new IllegalArgumentException("Team ID and submission cannot be null");

        Team team = teamRepository.findByID(teamID);
        if (team == null)
            throw new IllegalArgumentException("Team not found");

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

        Submission submission = submissionRepository.findByID(submissionID);
        if (submission == null)
            throw new IllegalArgumentException("Submission not found");

        submission.setName(name);
        submission.setSubmissionOnDate(LocalDate.now());
        submissionRepository.save(submission);

        return submission;
    }

    /**
     * Valuta una sottomissione.
     * Segue il sequence diagram di "Valuta Sottomissione":
     * 1. Verifica dati validi
     * 2. Recupera il giudice e verifica che esista
     * 3. Recupera la submission e verifica che esista
     * 4. Verifica che non sia già stata valutata
     * 5. Delega la valutazione a Judge.evaluateSubmission()
     * 6. Salva la submission aggiornata
     */
    public Evaluation evaluateSubmission(Long judgeID, Long submissionID, int grade, String briefJudgment) {
        // 1. Verifica dati validi
        if (judgeID == null || submissionID == null
                || briefJudgment == null || briefJudgment.trim().isEmpty())
            throw new IllegalArgumentException("Invalid data");

        // 2. Recupera il giudice
        StaffMember staff = staffMemberRepository.findByID(judgeID);
        if (!(staff instanceof Judge))
            throw new IllegalArgumentException("Judge not found");
        Judge judge = (Judge) staff;

        // 3. Recupera la submission
        Submission submission = submissionRepository.findByID(submissionID);
        if (submission == null)
            throw new IllegalArgumentException("Submission not found");

        // 4. Verifica che non sia già stata valutata
        if (submission.getGrade() != null)
            throw new IllegalArgumentException("Submission already evaluated");

        // 5. Delega la valutazione all'entità Judge
        Evaluation evaluation = judge.evaluateSubmission(submission, grade, briefJudgment);

        // 6. Salva la submission aggiornata con la valutazione
        submissionRepository.save(submission);

        return evaluation;
    }

    /**
     * Modifica la valutazione di una sottomissione.
     * Segue il sequence diagram di "Modifica Valutazione Sottomissione":
     * 1. Verifica dati validi
     * 2. Recupera il giudice e verifica che esista
     * 3. Recupera l'hackathon e verifica che sia nello stato "in corso"
     * 4. Recupera la valutazione e verifica che esista
     * 5. Recupera la submission e verifica che esista
     * 6. Delega la modifica della valutazione a Judge.editEvaluateSubmission()
     * 7. Salva la submission aggiornata
     */
    public Evaluation editEvaluateSubmission(Long judgeID, Long submissionID, Long hackathonID,
                                             Long evaluationID, int grade, String briefJudgment) {
        // 1. Verifica dai validi
        if (judgeID == null || submissionID == null || evaluationID == null ||
                grade<0 || grade>10 || briefJudgment==null || briefJudgment.trim().isEmpty())
            throw new IllegalArgumentException("Invalid data");

        // 2. Recupera il giudice
        Judge judge = staffMemberRepository.getJudge(judgeID);

        // 3. Recupera l'hackathon
        Hackathon hackathon = hackathonRepository.findByID(hackathonID);

        // 4. Verifica che l'hackathon sia nello stato "in corso"
        hackathon.isRegistrationOpen();

        // 5. Recupera la valutazione da modificare
        Evaluation evaluation = submissionRepository.findEvaluationByID(evaluationID);
        if(evaluation == null)
            throw new IllegalArgumentException("Evaluation not found");

        // 6. Recupera la sottomissione valutata
        Submission submission = submissionRepository.findByID(submissionID);
        if (submission == null)
            throw new IllegalArgumentException("Submission not found");

        // 7. Delega la modifica della valutazione all'entità Judge
        judge.editEvaluateSubmission(submission, evaluation, grade, briefJudgment);

        // 8. Salva la submission aggiornata con la valutazione
        submissionRepository.save(submission);

        return evaluation;
    }

}