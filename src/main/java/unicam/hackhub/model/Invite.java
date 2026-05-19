package unicam.hackhub.model;

import java.time.LocalDate;

public class Invite {

    private Long id;
    private LocalDate inviteDate;
    //private InviteState status;

    public Invite() {

    }

    public Invite(Long id, User invitedUser, Team team) {
        this.id = id;
        this.inviteDate = LocalDate.now();
    }

    public Long getId() {
        return id;
    }

    public LocalDate getInviteDate() {
        return inviteDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setInviteDate(LocalDate inviteDate) {
        this.inviteDate = inviteDate;
    }

//    public InviteState getStatus() {
//        return status;
//    }
//
//    public InviteState setStatus(Long id) {
//        this.status = status;
//    }


}