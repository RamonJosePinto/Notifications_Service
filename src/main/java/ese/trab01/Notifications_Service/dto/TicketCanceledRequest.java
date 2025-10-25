// dto/TicketCanceledRequest.java
package ese.trab01.Notifications_Service.dto;

public record TicketCanceledRequest(
        Long participantId,
        Long eventId,
        Long ticketId,
        String reason // opcional
) {}
