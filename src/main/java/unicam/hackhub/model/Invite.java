package unicam.hackhub.model;

import unicam.hackhub.model.enums.InviteState;

import java.time.LocalDate;

public class Invite {

    private Long id;
    private User invitedUser;
    private Team team;
    private LocalDate inviteDate;
    private InviteState status;

    public Invite() {}

    public Invite(Long id, User invitedUser, Team team) {
        this.id = id;
        this.invitedUser = invitedUser;
        this.team = team;
        this.inviteDate = LocalDate.now();
        this.status = InviteState.PENDING;
    }

    public Long getId() { return id; }
    public User getInvitedUser() { return invitedUser; }
    public Team getTeam() { return team; }
    public LocalDate getInviteDate() { return inviteDate; }
    public InviteState getStatus() { return status; }

    public void setId(Long id) { this.id = id; }
    public void setInvitedUser(User invitedUser) { this.invitedUser = invitedUser; }
    public void setTeam(Team team) { this.team = team; }
    public void setInviteDate(LocalDate inviteDate) { this.inviteDate = inviteDate; }
    public void setStatus(InviteState status) { this.status = status; }
}