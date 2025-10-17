package ese.trab01.Notifications_Service.service.provider;

public interface SmsSenderPort {
    boolean send(String to, String body);
}
