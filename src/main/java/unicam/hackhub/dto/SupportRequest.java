package unicam.hackhub.dto;

import java.util.List;

public record SupportRequest(
        Long leaderId,
        String name,
        List<Long> invitedUserIds
) {}