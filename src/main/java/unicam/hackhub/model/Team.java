package unicam.hackhub.model;

import java.util.List;

public class Team {

    private Long id;
    private String teamName;
    private Submission submission;
    private Hackathon hackathon;
    private List<User> members;

    public Team() {

    }

    public Team(Long id, String teamName, List<User> members) {
        this.id = id;
        this.teamName = teamName;
        this.members = members;
    }

    public Invite createInvite(Long userID) {

        return new Invite();
    }

    public void deleteInvite(Long inviteID) {
        // TODO
    }

    public int getSize() {
        if (members == null) {
            return 0;
        }

        return members.size();
    }

    public Long getId() {
        return id;
    }

    public String getTeamName() {
        return teamName;
    }

    public List<User> getMembers() {
        return members;
    }

    public Submission getSubmission() {
        return submission;
    }

    public Hackathon getHackathon() {
        return hackathon;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    public void setHackathon(Hackathon hackathon) {
        this.hackathon = hackathon;
    }

}