package ese.trab01.Notifications_Service.service;

import ese.trab01.Notifications_Service.dto.*;
import ese.trab01.Notifications_Service.exception.RecursoNaoEncontradoException;
import ese.trab01.Notifications_Service.model.Notification;
import ese.trab01.Notifications_Service.model.NotificationChannel;
import ese.trab01.Notifications_Service.model.NotificationStatus;
import ese.trab01.Notifications_Service.model.NotificationType;
import ese.trab01.Notifications_Service.repository.NotificationRepository;
import ese.trab01.Notifications_Service.service.provider.EmailSenderPort;
import ese.trab01.Notifications_Service.service.provider.SmsSenderPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.OffsetDateTime;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;
    private final EmailSenderPort emailSender;
    private final SmsSenderPort smsSender;

    @Transactional(readOnly = true)
    public Page<NotificationResponseDto> list(
            Integer page, Integer size,
            NotificationStatus status,
            NotificationChannel channel,
            NotificationType type,
            String recipient
    ) {
        Pageable pageable = PageRequest.of(
                page == null ? 0 : page,
                size == null ? 10 : size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        // Estratégia: aplica o filtro mais específico disponível
        if (status != null && channel != null && type != null) {
            return repository.findByStatusAndChannelAndType(status, channel, type, pageable).map(this::toDto);
        }
        if (status != null && channel != null) {
            return repository.findByStatusAndChannel(status, channel, pageable).map(this::toDto);
        }
        if (status != null && type != null) {
            return repository.findByStatusAndType(status, type, pageable).map(this::toDto);
        }
        if (channel != null && type != null) {
            return repository.findByChannelAndType(channel, type, pageable).map(this::toDto);
        }
        if (status != null) {
            return repository.findByStatus(status, pageable).map(this::toDto);
        }
        if (channel != null) {
            return repository.findByChannel(channel, pageable).map(this::toDto);
        }
        if (type != null) {
            return repository.findByType(type, pageable).map(this::toDto);
        }
        if (recipient != null && !recipient.isBlank()) {
            return repository.findByRecipientContainingIgnoreCase(recipient.trim(), pageable).map(this::toDto);
        }

        // sem filtros
        return repository.findAll(pageable).map(this::toDto);
    }

    // ==== APIs de alto nível: mapeiam RFs para templates simples ====

    @Transactional
    public NotificationResponseDto sendRegistrationConfirmation(RegistrationConfirmationRequest req) {
        var subject = "Confirmação de cadastro";
        var body = "Olá " + req.userName() + ", seu cadastro foi confirmado!";
        return sendEmail(req.recipientEmail(), subject, body, NotificationType.REGISTRATION_CONFIRMATION);
    }

    @Transactional
    public NotificationResponseDto sendPurchaseConfirmation(PurchaseConfirmationRequest req) {
        var subject = "Confirmação de pagamento";
        var body = "Pagamento confirmado! Reserva #" + req.reservationId()
                + " para o evento " + req.eventId()
                + " - quantidade: " + req.quantity();
        return sendEmail(req.recipientEmail(), subject, body, NotificationType.PURCHASE_CONFIRMATION);
    }

    @Transactional
    public NotificationResponseDto sendEventReminder(EventReminderRequest req) {
        var subject = "Lembrete de evento: " + req.eventName();
        var body = "Não esqueça! Seu evento " + req.eventName() + " acontece em "
                + req.eventDateTime() + ".";
        return sendEmail(req.recipientEmail(), subject, body, NotificationType.EVENT_REMINDER);
    }

    // ==== API genérica (manual) ====
    @Transactional
    public NotificationResponseDto sendGeneric(NotificationRequestDto req, NotificationType type) {
        NotificationChannel channel = req.getChannel() != null
                ? req.getChannel()
                : NotificationChannel.IN_APP; // fallback

        return switch (channel) {
            case EMAIL -> sendEmail(req.getRecipient(), req.getSubject(), req.getMessage(), type);
            case SMS   -> sendSms(req.getRecipient(), req.getMessage(), type);
            case IN_APP-> saveOnly(req.getRecipient(), req.getSubject(), req.getMessage(), type);
        };
    }


    // ==== Helpers (mock) ====
    private NotificationResponseDto sendEmail(String to, String subject, String body, NotificationType type) {
        var n = base(type, NotificationChannel.EMAIL, to, subject, body);
        repository.save(n);

        boolean ok = emailSender.send(to, subject, body);
        n.setStatus(ok ? NotificationStatus.SENT : NotificationStatus.FAILED);
        n.setSentAt(OffsetDateTime.now());
        repository.save(n);

        return toDto(n);
    }

    private NotificationResponseDto sendSms(String to, String body, NotificationType type) {
        var n = base(type, NotificationChannel.SMS, to, null, body);
        repository.save(n);

        boolean ok = smsSender.send(to, body);
        n.setStatus(ok ? NotificationStatus.SENT : NotificationStatus.FAILED);
        n.setSentAt(OffsetDateTime.now());
        repository.save(n);

        return toDto(n);
    }

    /** Grava no banco e considera “SENT” (mock IN_APP) */
    private NotificationResponseDto saveOnly(String recipient, String subject, String body, NotificationType type) {
        var n = base(type, NotificationChannel.IN_APP, recipient, subject, body);
        n.setStatus(NotificationStatus.SENT);
        n.setSentAt(OffsetDateTime.now());
        repository.save(n);
        log.info("[MOCK IN-APP] to={}, subject={}, body={}", recipient, subject, body);
        return toDto(n);
    }

    private Notification base(NotificationType type, NotificationChannel channel, String recipient, String subject, String body) {
        return Notification.builder()
                .type(type)
                .channel(channel)
                .recipient(recipient)
                .subject(subject)
                .message(body)
                .status(NotificationStatus.PENDING)
                .createdAt(OffsetDateTime.now())
                .build();
    }

    private NotificationResponseDto toDto(Notification n) {
        return new NotificationResponseDto(
                n.getId(), n.getType(), n.getChannel(), n.getRecipient(),
                n.getSubject(), n.getMessage(), n.getStatus(), n.getCreatedAt(), n.getSentAt()
        );
    }

    // ==== Queries ====
    @Transactional(readOnly = true)
    public NotificationResponseDto getById(Long id) {
        var n = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Notificação não encontrada"));
        return toDto(n);
    }
}
