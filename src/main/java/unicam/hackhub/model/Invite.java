package unicam.hackhub.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import unicam.hackhub.model.enums.InviteState;
import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(name = "invites")
public class Invite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "invited_user_id")
    private User invitedUser;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(nullable = false)
    private LocalDate inviteDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InviteState status;

    public Invite() {}

    public Invite(Long id, User invitedUser, Team team) {
        this.id = id;
        this.invitedUser = invitedUser;
        this.team = team;
        this.inviteDate = LocalDate.now();
        this.status = InviteState.PENDING;
    }

}