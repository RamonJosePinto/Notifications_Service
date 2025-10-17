package ese.trab01.Notifications_Service.service.provider;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MockSmsSender implements SmsSenderPort {
    @Override
    public boolean send(String to, String body) {
        log.info("[MOCK SMS] to={}, body={}", to, body);
        return true;
    }
}
