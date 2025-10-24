package ese.trab01.Notifications_Service.dto;

import lombok.Builder;

@Builder
public record RegistrationConfirmationRequest(
        Long participantId
) {}
