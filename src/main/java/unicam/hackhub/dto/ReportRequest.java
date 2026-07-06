package unicam.hackhub.dto;

public record ReportRequest(
        Long mentorId,
        Long teamId,
        Long hackathonId,
        String description
        ) {}
