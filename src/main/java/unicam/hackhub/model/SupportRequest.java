package unicam.hackhub.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(name = "support_requests")
public class SupportRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate requestedDate;

    @ManyToOne
    @JoinColumn(name = "hackathon_id", nullable = false)
    private Hackathon hackathon;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne
    @JoinColumn(name = "mentor_id", nullable = false)
    private Mentor mentor;

    @Column
    private String googleEventID;

    public SupportRequest() {}

    public SupportRequest(Long id, LocalDate requestedDate, Hackathon hackathon,
                          User user, Team team, Mentor mentor) {
        this.id = id;
        this.requestedDate = requestedDate;
        this.hackathon = hackathon;
        this.user = user;
        this.team = team;
        this.mentor = mentor;
    }
}