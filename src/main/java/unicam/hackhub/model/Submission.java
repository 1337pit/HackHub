package unicam.hackhub.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate submissionOnDate;

    @Column(nullable = false)
    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "evaluation_id")
    private Evaluation grade;

    public Submission() {}

    public Submission(Long id, String name) {
        this.id = id;
        this.name = name;
        this.submissionOnDate = LocalDate.now();
    }

}