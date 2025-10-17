package ese.trab01.Notifications_Service.dto;


import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record EventReminderRequest(
        String recipientEmail,
        Long eventId,
        String eventName,
        OffsetDateTime eventDateTime
) {}
