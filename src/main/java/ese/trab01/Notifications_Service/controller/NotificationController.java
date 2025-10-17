package ese.trab01.Notifications_Service.controller;


import ese.trab01.Notifications_Service.dto.*;
import ese.trab01.Notifications_Service.model.NotificationChannel;
import ese.trab01.Notifications_Service.model.NotificationStatus;
import ese.trab01.Notifications_Service.model.NotificationType;
import ese.trab01.Notifications_Service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @GetMapping
    public ResponseEntity<Page<NotificationResponseDto>> list(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) NotificationStatus status,
            @RequestParam(required = false) NotificationChannel channel,
            @RequestParam(required = false) NotificationType type,
            @RequestParam(required = false) String recipient
    ) {
        var result = service.list(page, size, status, channel, type, recipient);
        return ResponseEntity.ok(result);
    }

    // ---- Endpoints alinhados aos RFs ----
    @PostMapping("/registration")
    public ResponseEntity<NotificationResponseDto> registration(@RequestBody RegistrationConfirmationRequest req) {
        return ResponseEntity.ok(service.sendRegistrationConfirmation(req));
    }

    @PostMapping("/purchase")
    public ResponseEntity<NotificationResponseDto> purchase(@RequestBody PurchaseConfirmationRequest req) {
        return ResponseEntity.ok(service.sendPurchaseConfirmation(req));
    }

    @PostMapping("/reminder")
    public ResponseEntity<NotificationResponseDto> reminder(@RequestBody EventReminderRequest req) {
        return ResponseEntity.ok(service.sendEventReminder(req));
    }

    // ---- Genérico (útil para testes) ----
    @PostMapping
    public ResponseEntity<NotificationResponseDto> generic(
            @RequestParam(defaultValue = "REGISTRATION_CONFIRMATION") NotificationType type,
            @RequestBody NotificationRequestDto body
    ) {
        return ResponseEntity.ok(service.sendGeneric(body, type));
    }

    // Consultar por ID
    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }
}
