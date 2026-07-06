package Model;

import org.junit.jupiter.api.BeforeEach;
import unicam.hackhub.model.Judge;
import unicam.hackhub.model.Submission;

import static org.junit.jupiter.api.Assertions.*;

class JudgeTest {

    private Judge judge;
    private Submission submission;

    @BeforeEach
    void setUp() {
        judge = new Judge(1L, "Judge One");
        submission = new Submission(1L, "Team Submission");
    }

}