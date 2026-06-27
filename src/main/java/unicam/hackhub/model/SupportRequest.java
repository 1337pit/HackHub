package unicam.hackhub.model;

import java.time.LocalDate;

public class SupportRequest {

    private Long id;
    private LocalDate requestedDate;
    private Long hackathonID;
    private Long userID;
    private Long teamID;
    private Long mentorID;
    private String googleEventID; // ID dell'evento creato su Google Calendar

    public SupportRequest(Long id, LocalDate requestedDate, Long hackathonID,
                          Long userID, Long teamID, Long mentorID) {
        this.id = id;
        this.requestedDate = requestedDate;
        this.hackathonID = hackathonID;
        this.userID = userID;
        this.teamID = teamID;
        this.mentorID = mentorID;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getRequestedDate() { return requestedDate; }

    public Long getHackathonID() { return hackathonID; }
    public void setHackathonID(Long hackathonID) { this.hackathonID = hackathonID; }

    public Long getUserID() { return userID; }
    public void setUserID(Long userID) { this.userID = userID; }

    public Long getTeamID() { return teamID; }
    public void setTeamID(Long teamID) { this.teamID = teamID; }

    public Long getMentorID() { return mentorID; }
    public void setMentorID(Long mentorID) { this.mentorID = mentorID; }

    public String getGoogleEventID() { return googleEventID; }
    public void setGoogleEventID(String googleEventID) { this.googleEventID = googleEventID; }
}