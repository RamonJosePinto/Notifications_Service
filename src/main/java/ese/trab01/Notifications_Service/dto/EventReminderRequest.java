package ese.trab01.Notifications_Service.dto;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record EventReminderRequest(
        Long participantId,
        Long eventId,
        OffsetDateTime eventDateTime,
        String eventName
) {}
