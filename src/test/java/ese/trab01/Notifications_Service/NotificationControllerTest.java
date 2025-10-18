package ese.trab01.Notifications_Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ese.trab01.Notifications_Service.controller.NotificationController;
import ese.trab01.Notifications_Service.dto.NotificationResponseDto;
import ese.trab01.Notifications_Service.model.NotificationChannel;
import ese.trab01.Notifications_Service.model.NotificationStatus;
import ese.trab01.Notifications_Service.model.NotificationType;
import ese.trab01.Notifications_Service.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class NotificationControllerTest {

    private MockMvc mvc;
    private ObjectMapper om;
    private NotificationService service; // mock
    private NotificationController controller;

    @BeforeEach
    void setup() {
        service = Mockito.mock(NotificationService.class);
        controller = new NotificationController(service);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
        om = new ObjectMapper();
    }

    @Test
    void purchase_deveRetornar200() throws Exception {
        when(service.sendPurchaseConfirmation(any()))
                .thenReturn(new NotificationResponseDto(1L, NotificationType.PURCHASE_CONFIRMATION,
                        NotificationChannel.EMAIL, "ramon@example.com", "Confirmação", "ok",
                        NotificationStatus.SENT, null, null));

        var body = """
            {"recipientEmail":"ramon@example.com","eventId":1001,"reservationId":10,"quantity":2}
        """;

        mvc.perform(post("/notifications/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void registration_deveRetornar200() throws Exception {
        when(service.sendRegistrationConfirmation(any()))
                .thenReturn(new NotificationResponseDto(2L, NotificationType.REGISTRATION_CONFIRMATION,
                        NotificationChannel.EMAIL, "u@ex.com", "Cadastro", "ok",
                        NotificationStatus.SENT, null, null));

        var body = """
            {"recipientEmail":"u@ex.com","userName":"Ramon"}
        """;

        mvc.perform(post("/notifications/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void reminder_deveRetornar200() throws Exception {
        when(service.sendEventReminder(any()))
                .thenReturn(new NotificationResponseDto(3L, NotificationType.EVENT_REMINDER,
                        NotificationChannel.EMAIL, "u@ex.com", "Lembrete", "ok",
                        NotificationStatus.SENT, null, null));

        var body = """
            {"recipientEmail":"u@ex.com","eventId":999,"eventName":"Show","eventDateTime":"2030-01-01T12:00:00Z"}
        """;

        mvc.perform(post("/notifications/reminder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void list_deveRetornar200() throws Exception {
        // Aqui não precisamos mockar retorno de Page para validar o status 200 no standalone
        mvc.perform(get("/notifications")
                        .param("page","0")
                        .param("size","5")
                        .param("status","SENT")
                        .param("channel","EMAIL")
                        .param("type","PURCHASE_CONFIRMATION"))
                .andExpect(status().isOk());
    }

    @Test
    void getById_deveRetornar200() throws Exception {
        when(service.getById(1L))
                .thenReturn(new NotificationResponseDto(1L, NotificationType.PURCHASE_CONFIRMATION,
                        NotificationChannel.EMAIL, "ramon@example.com", "Confirmação", "ok",
                        NotificationStatus.SENT, null, null));

        mvc.perform(get("/notifications/1"))
                .andExpect(status().isOk());
    }
}
