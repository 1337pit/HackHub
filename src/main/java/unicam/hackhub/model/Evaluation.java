package unicam.hackhub.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "evaluations")
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String briefJudgement;

    @Column(nullable = false)
    private int grade;

    public Evaluation() {}

    public Evaluation(String briefJudgement, int grade) {
        this.briefJudgement = briefJudgement;
        this.grade = grade;
    }

}
