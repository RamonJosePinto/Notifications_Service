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
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    private NotificationChannel channel;

    private String recipient;       // email, telefone, userId, etc.
    private String subject;         // opcional p/ email
    @Column(length = 4000)
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    private OffsetDateTime createdAt;
    private OffsetDateTime sentAt;

    @Column(length = 2000)
    private String metadata;        // JSON/texto livre com extras (opcional)
}