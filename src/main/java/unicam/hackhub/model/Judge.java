package unicam.hackhub.model;

public class Judge implements StaffMember {

    private Long id;
    private String name;
    private String email;
    private Hackathon hackathon;

    public Judge() {

    }

    public Judge(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Evaluation evaluateSubmission(Submission submission, int grade) {
        // TODO
        return new Evaluation();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {return email;}

    public Hackathon getHackathon() {
        return hackathon;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {this.email = email;}

    public void setHackathon(Hackathon hackathon) {
        this.hackathon = hackathon;
    }

}