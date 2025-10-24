package ese.trab01.Notifications_Service;

import ese.trab01.Notifications_Service.dto.EventReminderRequest;
import ese.trab01.Notifications_Service.dto.PurchaseConfirmationRequest;
import ese.trab01.Notifications_Service.dto.RegistrationConfirmationRequest;
import ese.trab01.Notifications_Service.model.Notification;
import ese.trab01.Notifications_Service.model.NotificationChannel;
import ese.trab01.Notifications_Service.model.NotificationStatus;
import ese.trab01.Notifications_Service.model.NotificationType;
import ese.trab01.Notifications_Service.repository.NotificationRepository;
import ese.trab01.Notifications_Service.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    private NotificationRepository repo;
    private NotificationService service;

    @BeforeEach
    void setup() {
        repo = mock(NotificationRepository.class);
        service = new NotificationService(repo);
        when(repo.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void purchaseConfirmation_salvaNotificacao() {
        var req = new PurchaseConfirmationRequest(23L, 1L, 10L);

        service.purchaseConfirmation(req);

        ArgumentCaptor<Notification> cap = ArgumentCaptor.forClass(Notification.class);
        verify(repo).save(cap.capture());
        var n = cap.getValue();
        assertEquals(NotificationType.PURCHASE_CONFIRMATION, n.getType());
        assertEquals(NotificationChannel.EMAIL, n.getChannel());
        assertEquals(NotificationStatus.PENDING, n.getStatus());
        assertEquals(23L, n.getParticipantId());
        assertEquals(1L, n.getEventId());
        assertEquals(10L, n.getTicketId());
    }

    @Test
    void registrationConfirmation_salvaNotificacao() {
        var req = new RegistrationConfirmationRequest(23L);

        service.registrationConfirmation(req);

        ArgumentCaptor<Notification> cap = ArgumentCaptor.forClass(Notification.class);
        verify(repo).save(cap.capture());
        var n = cap.getValue();
        assertEquals(NotificationType.REGISTRATION_CONFIRMATION, n.getType());
        assertEquals(23L, n.getParticipantId());
        assertEquals(1L, n.getEventId());
    }

    @Test
    void eventReminder_salvaNotificacao() {
        var req = new EventReminderRequest(23L, 1L, null, null);

        service.eventReminder(req);

        ArgumentCaptor<Notification> cap = ArgumentCaptor.forClass(Notification.class);
        verify(repo).save(cap.capture());
        var n = cap.getValue();
        assertEquals(NotificationType.EVENT_REMINDER, n.getType());
        assertEquals(23L, n.getParticipantId());
        assertEquals(1L, n.getEventId());
    }
}
