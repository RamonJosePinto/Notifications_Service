package ese.trab01.Notifications_Service.service.provider;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MockEmailSender implements EmailSenderPort {
    @Override
    public boolean send(String to, String subject, String body) {
        log.info("[MOCK EMAIL] to={}, subject={}, body={}", to, subject, body);
        return true; // sempre “sucesso” no mock
    }
}
