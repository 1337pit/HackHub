package unicam.hackhub.model;

import java.util.List;
import java.util.Scanner;

public class RequestSupport {

    private Long id;
    private Mentor.Slot slot;
    private Long hackathonID;
    private Long userID;
    private Long teamID;
    private Long mentorID;

    public RequestSupport(Long id, Mentor.Slot slot, Long hackathonID, Long userID, Long teamID, Long mentorID) {
        this.id = id;
        this.slot = slot;
        this.hackathonID  = hackathonID;
        this.userID = userID;
        this.teamID = teamID;
        this.mentorID = mentorID;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Mentor.Slot getSlot() {
        return slot;
    }
    public void setSlot(Mentor.Slot slot) {
        this.slot = slot;
    }

    public Long getHackathonID() {
        return hackathonID;
    }
    public void setHackathonID(Long hackathonID) {
        this.hackathonID = hackathonID;
    }

    public Long getUserID() {
        return userID;
    }
    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Long getTeamID() {
        return teamID;
    }
    public void setTeamID(Long teamID) {
        this.teamID = teamID;
    }

    public Long getMentorID() {
        return mentorID;
    }
    public void setMentorID(Long mentorID) {
        this.mentorID = mentorID;
    }

}
