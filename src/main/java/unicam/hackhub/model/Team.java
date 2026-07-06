package unicam.hackhub.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import unicam.hackhub.model.observer.HackathonObserver;
import unicam.hackhub.model.state.*;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "teams")
public class Team implements HackathonObserver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String teamName;

    // Relazione con User (chi "possiede" il team come riferimento)
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "submission_id")
    private Submission submission;

    @ManyToOne
    @JoinColumn(name = "hackathon_id")
    @JsonIgnore
    private Hackathon hackathon;

    @ManyToMany
    @JoinTable(
            name = "team_members",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> members;

    public Team() {}

    public Team(Long id, String teamName, List<User> members) {
        this.id = id;
        this.teamName = teamName;
        this.members = members;
    }

    /**
     * Notifica il Team del cambio di stato dell'Hackathon.
     * Il Team è interessato alla transizione a InProgressState
     * (può iniziare a caricare submission) e a ConcludedState.
     */
    @Override
    public void update(HackathonState newState) {
        if (newState instanceof InProgressState)
            System.out.println("Team [" + teamName + "] notified: hackathon started.");
        else if (newState instanceof EvaluationState)
            System.out.println("Team [" + teamName + "] notified: submission phase ended.");
        else if (newState instanceof ConcludedState)
            System.out.println("Team [" + teamName + "] notified: hackathon concluded.");
    }

    public int getSize() { return members == null ? 0 : members.size(); }

}