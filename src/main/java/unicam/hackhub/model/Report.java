package unicam.hackhub.model;

public class Report {
    private Long id;
    private String description;
    private Team team;
    private Mentor mentor;
    private Hackathon hackathon;

    public Report() {}

    public Report(Long id, String description, Team team,
                           Mentor mentor, Hackathon hackathon) {
        this.id = id;
        this.description = description;
        this.team = team;
        this.mentor = mentor;
        this.hackathon = hackathon;
    }

    public Long getId() { return id; }
    public String getDescription() { return description; }
    public Team getTeam() { return team; }
    public Mentor getMentor() { return mentor; }
    public Hackathon getHackathon() { return hackathon; }

    public void setId(Long id) { this.id = id; }
    public void setDescription(String description) { this.description = description; }
    public void setTeam(Team team) { this.team = team; }
    public void setMentor(Mentor mentor) { this.mentor = mentor; }
    public void setHackathon(Hackathon hackathon) { this.hackathon = hackathon; }
}
