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
        System.out.println("Team Vincente: " + team);
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

    /**
     * Modifica un hackathon.
     */
    public Hackathon editHackathon(String name, String rulebook, LocalDate registrationDeadline,
                                   String location, String prize, int maxTeamSize, Judge judge, Mentor mentor) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        hackathon.setNameHackathon(name);
        if (rulebook == null) {
            throw new IllegalArgumentException("rulebook cannot be null");
        }
        hackathon.setRulebook(rulebook);
        if (registrationDeadline == null) {
            throw new IllegalArgumentException("registrationDeadline cannot be null");
        }
        hackathon.setRegistrationDeadline(registrationDeadline);
        if (location == null) {
            throw new IllegalArgumentException("location cannot be null");
        }
        hackathon.setLocation(location);
        if (prize == null) {
            throw new IllegalArgumentException("prize cannot be null");
        }
        hackathon.setPrize(prize);
        if (maxTeamSize <= 0) {
            throw new IllegalArgumentException("maxTeamSize cannot be <= 0");
        }
        hackathon.setMaxTeamSize(maxTeamSize);
        if (judge == null) {
            throw new IllegalArgumentException("judge cannot be null");
        }
        hackathon.setJudge(judge);

        hackathon.setMentor(mentor);

        return hackathon;
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