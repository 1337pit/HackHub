package unicam.hackhub.dto;

public record EditEvaluationRequest(
        Long judgeId,
        Long hackathonId,
        Long evaluationId,
        int grade,
        String briefJudgment
) {
}
