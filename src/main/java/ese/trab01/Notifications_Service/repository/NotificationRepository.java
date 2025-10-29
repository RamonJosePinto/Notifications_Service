package ese.trab01.Notifications_Service.repository;

import ese.trab01.Notifications_Service.model.Notification;
import ese.trab01.Notifications_Service.model.NotificationChannel;
import ese.trab01.Notifications_Service.model.NotificationStatus;
import ese.trab01.Notifications_Service.model.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByStatus(NotificationStatus status, Pageable pageable);

    Page<Notification> findByChannel(NotificationChannel channel, Pageable pageable);

    Page<Notification> findByType(NotificationType type, Pageable pageable);

    Page<Notification> findByParticipantId(UUID participantId, Pageable pageable);

    Page<Notification> findByRecipientContainingIgnoreCase(String recipient, Pageable pageable);

    // Combinações úteis (opcional):
    Page<Notification> findByStatusAndChannel(NotificationStatus status, NotificationChannel channel, Pageable pageable);

    Page<Notification> findByStatusAndType(NotificationStatus status, NotificationType type, Pageable pageable);

    Page<Notification> findByChannelAndType(NotificationChannel channel, NotificationType type, Pageable pageable);

    Page<Notification> findByStatusAndChannelAndType(
            NotificationStatus status, NotificationChannel channel, NotificationType type, Pageable pageable
    );
}