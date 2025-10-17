package ese.trab01.Notifications_Service.service.provider;


public interface EmailSenderPort {
    boolean send(String to, String subject, String body);
}
