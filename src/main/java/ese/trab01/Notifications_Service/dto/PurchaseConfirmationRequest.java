package ese.trab01.Notifications_Service.dto;

import lombok.Builder;

@Builder
public record PurchaseConfirmationRequest(
        String recipientEmail,
        Long eventId,
        Long reservationId,
        Integer quantity
) {}