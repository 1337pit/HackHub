package unicam.hackhub.model;

public class Judge implements StaffMember {

    private Long id;
    private String name;
    private String email;
    private Hackathon hackathon;

    public Judge() {}

    public Judge(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Valuta una sottomissione con un voto e un giudizio.
     * Crea un oggetto Evaluation e lo associa alla Submission.
     */
    public Evaluation evaluateSubmission(Submission submission, int grade, String briefJudgment) {
        if (submission == null)
            throw new IllegalArgumentException("Submission cannot be null");
        if (grade < 0 || grade > 10)
            throw new IllegalArgumentException("Grade must be between 0 and 10");
        if (briefJudgment == null || briefJudgment.trim().isEmpty())
            throw new IllegalArgumentException("Brief judgment cannot be empty");

        Evaluation evaluation = new Evaluation(briefJudgment, grade);
        submission.setGrade(evaluation);
        return evaluation;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Hackathon getHackathon() { return hackathon; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setHackathon(Hackathon hackathon) { this.hackathon = hackathon; }
}