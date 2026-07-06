package unicam.hackhub.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "registrations")
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne
    @JoinColumn(name = "hackathon_id", nullable = false)
    @JsonBackReference("hackathon-registrations")
    private Hackathon hackathon;

    @Column(nullable = false)
    private LocalDate registrationDate;

    public Registration() {}

    public Registration(Long id, Team team, Hackathon hackathon) {
        this.id = id;
        this.team = team;
        this.hackathon = hackathon;
        this.registrationDate = LocalDate.now();
    }

    public boolean exists() {
        return id != null && team != null && hackathon != null && registrationDate != null;
    }
}