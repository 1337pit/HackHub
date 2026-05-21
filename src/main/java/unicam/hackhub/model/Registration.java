package unicam.hackhub.model;

import java.time.LocalDate;

public class Registration {

    private Long id;
    private Team team;
    private Hackathon hackathon;
    private LocalDate registrationDate;

    public Registration(Long id, Team team, Hackathon hackathon) {
        this.id = id;
        this.team = team;
        this.hackathon = hackathon;
        registrationDate = LocalDate.now();
    }

    public Long getId() {
        return id;
    }
    public Team getTeam() { return team; }
    public Hackathon getHackathon() { return hackathon; }
    public LocalDate getRegistrationDate() { return registrationDate; }

    public void setId(Long id) {
        this.id = id;
    }
    public void setTeam(Team team) { this.team = team; }
    public void setHackathon(Hackathon hackathon) { this.hackathon = hackathon; }

    public boolean exists(){
        return id != null && team != null && hackathon != null && registrationDate != null;
    }
}
