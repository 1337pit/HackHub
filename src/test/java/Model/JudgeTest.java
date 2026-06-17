package Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import unicam.hackhub.model.Evaluation;
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

    @Test
    @DisplayName("evaluateSubmission - valid data returns evaluation and updates submission")
    void evaluateSubmission_validData_returnsEvaluationAndUpdatesSubmission() {
        Evaluation evaluation = judge.evaluateSubmission(submission, 8, "Great work");

        assertNotNull(evaluation);
        assertEquals(8, evaluation.getGrade());
        assertEquals("Great work", evaluation.getBriefJudgement());
        assertSame(evaluation, submission.getGrade());
    }

    @Test
    @DisplayName("evaluateSubmission - null submission throws exception")
    void evaluateSubmission_nullSubmission_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> judge.evaluateSubmission(null, 8, "Great work"));

        assertEquals("Submission cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("evaluateSubmission - grade below 0 throws exception")
    void evaluateSubmission_gradeBelowZero_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> judge.evaluateSubmission(submission, -1, "Great work"));

        assertEquals("Grade must be between 0 and 10", exception.getMessage());
    }

    @Test
    @DisplayName("evaluateSubmission - grade above 10 throws exception")
    void evaluateSubmission_gradeAboveTen_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> judge.evaluateSubmission(submission, 11, "Great work"));

        assertEquals("Grade must be between 0 and 10", exception.getMessage());
    }

    @Test
    @DisplayName("evaluateSubmission - boundary grades 0 and 10 are valid")
    void evaluateSubmission_boundaryGrades_areValid() {
        Evaluation lowEvaluation = judge.evaluateSubmission(submission, 0, "Needs improvement");
        assertEquals(0, lowEvaluation.getGrade());

        Evaluation highEvaluation = judge.evaluateSubmission(submission, 10, "Outstanding");
        assertEquals(10, highEvaluation.getGrade());
    }

    @Test
    @DisplayName("evaluateSubmission - null brief judgment throws exception")
    void evaluateSubmission_nullBriefJudgment_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> judge.evaluateSubmission(submission, 8, null));

        assertEquals("Brief judgment cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("evaluateSubmission - empty or blank brief judgment throws exception")
    void evaluateSubmission_blankBriefJudgment_throwsException() {
        IllegalArgumentException exceptionEmpty = assertThrows(IllegalArgumentException.class,
                () -> judge.evaluateSubmission(submission, 8, ""));
        assertEquals("Brief judgment cannot be empty", exceptionEmpty.getMessage());

        IllegalArgumentException exceptionBlank = assertThrows(IllegalArgumentException.class,
                () -> judge.evaluateSubmission(submission, 8, "   "));
        assertEquals("Brief judgment cannot be empty", exceptionBlank.getMessage());
    }

}
