package unicam.hackhub.model;

public class Mentor implements StaffMember {

    private Long id;
    private String name;
    private String email;
    private Hackathon hackathon;

    public Mentor() {

    }

    public Mentor(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public void proposeCall() {
        // TODO
    }

    public void reportTeam(Team team, String description) {
        // TODO
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