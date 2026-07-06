package unicam.hackhub.dto;

public record EvaluateSubmissionRequest(
        Long judgeId,
        int grade,
        String briefJudgment
) {
}
