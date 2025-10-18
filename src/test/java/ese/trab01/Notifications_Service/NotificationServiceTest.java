package ese.trab01.Notifications_Service;

import ese.trab01.Notifications_Service.dto.*;
import ese.trab01.Notifications_Service.repository.NotificationRepository;
import ese.trab01.Notifications_Service.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ese.trab01.Notifications_Service.model.Notification;
import ese.trab01.Notifications_Service.model.NotificationChannel;
import ese.trab01.Notifications_Service.model.NotificationStatus;
import ese.trab01.Notifications_Service.model.NotificationType;
import ese.trab01.Notifications_Service.repository.NotificationRepository;
import ese.trab01.Notifications_Service.service.provider.EmailSenderPort;
import ese.trab01.Notifications_Service.service.provider.SmsSenderPort;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    NotificationRepository repository;
    @Mock EmailSenderPort emailSender;
    @Mock SmsSenderPort smsSender;

    @InjectMocks
    NotificationService service;

    @BeforeEach
    void setup() {
        // Esses stubs podem não ser usados em todos os testes -> use lenient()
        lenient().when(repository.save(any(Notification.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        lenient().when(emailSender.send(anyString(), anyString(), anyString()))
                .thenReturn(true);
        lenient().when(smsSender.send(anyString(), anyString()))
                .thenReturn(true);
    }

    @Test
    void sendPurchaseConfirmation_deveSalvarENotificarEmail() {
        var req = PurchaseConfirmationRequest.builder()
                .recipientEmail("ramon@example.com")
                .eventId(1001L).reservationId(77L).quantity(2)
                .build();

        NotificationResponseDto dto = service.sendPurchaseConfirmation(req);

        assertThat(dto.type()).isEqualTo(NotificationType.PURCHASE_CONFIRMATION);
        assertThat(dto.channel()).isEqualTo(NotificationChannel.EMAIL);
        assertThat(dto.status()).isEqualTo(NotificationStatus.SENT);

        verify(emailSender).send(eq("ramon@example.com"), contains("Confirmação"), contains("Pagamento confirmado"));
        verify(repository, atLeast(2)).save(any(Notification.class)); // PENDING -> SENT
    }

    @Test
    void sendRegistrationConfirmation_deveUsarTemplate() {
        var req = RegistrationConfirmationRequest.builder()
                .recipientEmail("user@ex.com").userName("Ramon").build();

        var dto = service.sendRegistrationConfirmation(req);
        assertThat(dto.type()).isEqualTo(NotificationType.REGISTRATION_CONFIRMATION);
        assertThat(dto.channel()).isEqualTo(NotificationChannel.EMAIL);
        assertThat(dto.status()).isEqualTo(NotificationStatus.SENT);
        verify(emailSender).send(eq("user@ex.com"), contains("Confirmação de cadastro"), contains("Ramon"));
    }

    @Test
    void sendEventReminder_deveUsarTemplate() {
        var req = EventReminderRequest.builder()
                .recipientEmail("user@ex.com")
                .eventId(999L)
                .eventName("Show da Banda X")
                .eventDateTime(OffsetDateTime.now().plusDays(1))
                .build();

        var dto = service.sendEventReminder(req);
        assertThat(dto.type()).isEqualTo(NotificationType.EVENT_REMINDER);
        assertThat(dto.channel()).isEqualTo(NotificationChannel.EMAIL);
        assertThat(dto.status()).isEqualTo(NotificationStatus.SENT);
        verify(emailSender).send(eq("user@ex.com"), contains("Lembrete de evento"), contains("Show da Banda X"));
    }

    @Test
    void sendGeneric_email() {
        var req = NotificationRequestDto.builder()
                .recipient("a@a.com")
                .subject("S")
                .message("M")
                .channel(NotificationChannel.EMAIL) // se seu DTO usa enum, troque por NotificationChannel.EMAIL
                .build();

        var dto = service.sendGeneric(req, NotificationType.REGISTRATION_CONFIRMATION);
        assertThat(dto.channel()).isEqualTo(NotificationChannel.EMAIL);
        assertThat(dto.status()).isEqualTo(NotificationStatus.SENT);
        verify(emailSender).send("a@a.com", "S", "M");
    }

    @Test
    void sendGeneric_sms() {
        var req = NotificationRequestDto.builder()
                .recipient("+5511999999999")
                .message("Oi")
                .channel(NotificationChannel.SMS) // se seu DTO usa enum, troque por NotificationChannel.SMS
                .build();

        var dto = service.sendGeneric(req, NotificationType.PURCHASE_CONFIRMATION);
        assertThat(dto.channel()).isEqualTo(NotificationChannel.SMS);
        assertThat(dto.status()).isEqualTo(NotificationStatus.SENT);
        verify(smsSender).send("+5511999999999", "Oi");
    }

    @Test
    void sendGeneric_inApp() {
        var req = NotificationRequestDto.builder()
                .recipient("user-id-123")
                .subject("Olá")
                .message("Bem-vindo")
                .channel(NotificationChannel.IN_APP) // ou NotificationChannel.IN_APP
                .build();

        var dto = service.sendGeneric(req, NotificationType.EVENT_REMINDER);
        assertThat(dto.channel()).isEqualTo(NotificationChannel.IN_APP);
        assertThat(dto.status()).isEqualTo(NotificationStatus.SENT);
        verifyNoInteractions(smsSender);
        verify(repository, atLeastOnce()).save(any(Notification.class));
    }
}
