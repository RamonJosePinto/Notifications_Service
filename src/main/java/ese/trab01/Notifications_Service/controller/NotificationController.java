package ese.trab01.Notifications_Service.controller;

import ese.trab01.Notifications_Service.dto.*;
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

    // ===== Endpoints usados por outros serviços =====

    @PostMapping("/purchase-confirmation")
    public ResponseEntity<Void> purchase(@RequestBody PurchaseConfirmationRequest req) {
        service.purchaseConfirmation(req);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/registration-confirmation")
    public ResponseEntity<Void> registration(@RequestBody RegistrationConfirmationRequest req) {
        service.registrationConfirmation(req);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/event-reminder")
    public ResponseEntity<Void> reminder(@RequestBody EventReminderRequest req) {
        service.eventReminder(req);
        return ResponseEntity.noContent().build();
    }

    // ===== Genérico (facilitador de testes) =====

//    @PostMapping
//    public ResponseEntity<NotificationResponseDto> generic(
//            @RequestParam(defaultValue = "REGISTRATION_CONFIRMATION") NotificationType type,
//            @RequestBody NotificationRequestDto body
//    ) {
//        return ResponseEntity.ok(service.sendGeneric(body, type));
//    }

    // ===== Consultas =====

    @GetMapping("/find/{id}")
    public ResponseEntity<NotificationResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<NotificationResponseDto>> list(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) NotificationType type
    ) {
        return ResponseEntity.ok(service.list(page, size, type));
    }
}
