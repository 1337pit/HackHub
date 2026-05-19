package unicam.hackhub.model;

import java.time.LocalDate;

public class Organizer implements StaffMember {

    private Long id;
    private String name;
    private String email;
    private Hackathon hackathon;

    public Organizer() {

    }

    public Organizer(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public void declareWinner(Team team) {
        // TODO
    }

//    public void evaluateReport(ViolationReport report) {
//        // TODO
//    }

    public void banTeam(Team team) {
        // TODO
    }

    public Hackathon createHackathon(Long id, String name, LocalDate registrationDate, LocalDate startDate,
                                LocalDate endDate, String location, String prize,
                                HackathonState state, int maxTeamSize, Long mentorID, Long judgeID) {

//        Hackathon hackathon = new Hackathon(id, name, registrationDate, startDate, endDate, location, prize,
//                                                state, maxTeamSize,);
//        // TODO
        return null;
    }

    public void addMentor(Mentor mentor) {
        // TODO
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() { return email;}

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