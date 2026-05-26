package unicam.hackhub.model;

import java.time.LocalDate;

public class Hackathon implements HackathonState {

    private Long id;
    private String nameHackathon;
    private String rulebook;
    private LocalDate registrationDeadline;
    private LocalDate startDate;
    private LocalDate endDate;
    private String location;
    private String prize;
    private HackathonState state;
    private int maxTeamSize;
    private Organizer organizer;
    private Judge judge;
    private Mentor[] mentor;

    public Hackathon() {

    }

    public Hackathon(Long id, String nameHackathon) {
        this.id = id;
        this.nameHackathon = nameHackathon;
    }

    public Hackathon(String name, String rulebook, LocalDate registrationDeadline, LocalDate startDate,
                     LocalDate endDate, String location, String prize, HackathonState state,
                     int maxTeamSize, Organizer organizer, Judge judge, Mentor[] mentor) {
        this.nameHackathon = name;
        this.rulebook = rulebook;
        this.registrationDeadline = registrationDeadline;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.prize = prize;
        this.state = state;
        this.maxTeamSize = maxTeamSize;
        this.organizer = organizer;
        this.mentor = mentor;
        this.judge = judge;
    }

    @Override
    public void onRegisterTeam() {
        if (state == null) {
            throw new IllegalStateException("Hackathon state is not defined");
        }

        state.onRegisterTeam();
    }

    @Override
    public void onUpload() {
        if (state == null) {
            throw new IllegalStateException("Hackathon state is not defined");
        }

        state.onUpload();
    }

    public boolean isRegistrationOpen() {
        if (registrationDeadline == null) {
            return false;
        }

        return !LocalDate.now().isAfter(registrationDeadline);
    }

    public Long getId() {
        return id;
    }

    public String getNameHackathon() {
        return nameHackathon;
    }

    public String getRulebook() {
        return rulebook;
    }

    public LocalDate getRegistrationDeadline() {
        return registrationDeadline;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getLocation() {
        return location;
    }

    public String getPrize() {
        return prize;
    }

    public HackathonState getState() { return state; }

    public int getMaxTeamSize() {
        return maxTeamSize;
    }

    public Organizer getOrganizer() {
        return organizer;
    }

    public Judge getJudge() {
        return judge;
    }

    public Mentor[] getMentor() {
        return mentor;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNameHackathon(String nameHackathon) {
        this.nameHackathon = nameHackathon;
    }

    public void setRulebook(String rulebook) {
        this.rulebook = rulebook;
    }

    public void setRegistrationDeadline(LocalDate registrationDeadline) {
        this.registrationDeadline = registrationDeadline;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }

    public void setState(HackathonState state) {this.state = state; }

    public void setMaxTeamSize(int maxTeamSize) {
        this.maxTeamSize = maxTeamSize;
    }

    public void setOrganizer(Organizer organizer) {
        this.organizer = organizer;
    }

    public void setJudge(Judge judge) {
        this.judge = judge;
    }

    public void setMentor(Mentor[] mentor) {
        this.mentor = mentor;
    }

}