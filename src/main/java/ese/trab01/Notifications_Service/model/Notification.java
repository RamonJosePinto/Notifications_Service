package ese.trab01.Notifications_Service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "notifications")
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class Notification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;          // REGISTRATION_CONFIRMATION, PURCHASE_CONFIRMATION, EVENT_REMINDER

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;    // EMAIL, SMS, IN_APP (usaremos IN_APP como padrão do mock)

    // >>> Novo modelo coerente com os demais serviços
    private Long participantId;
    private Long eventId;
    private Long ticketId;

    // >>> Mantidos para o endpoint genérico / compatibilidade
    private String recipient;               // e-mail, telefone, etc (opcional no mock)
    private String subject;                 // opcional
    @Column(length = 4000)
    private String message;                 // opcional

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;      // PENDING, SENT, FAILED (mock: PENDING)

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    private OffsetDateTime sentAt;

    @Column(length = 2000)
    private String metadata;                // JSON/texto livre com extras (opcional)
}
