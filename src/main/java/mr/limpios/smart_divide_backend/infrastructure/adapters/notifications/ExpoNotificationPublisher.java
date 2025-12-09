package mr.limpios.smart_divide_backend.infrastructure.adapters.notifications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.niamedtech.expo.exposerversdk.ExpoPushNotificationClient;
import com.niamedtech.expo.exposerversdk.request.PushNotification;
import com.niamedtech.expo.exposerversdk.response.TicketResponse;

import lombok.extern.slf4j.Slf4j;
import mr.limpios.smart_divide_backend.application.interfaces.NotificationPublisher;
import mr.limpios.smart_divide_backend.application.repositories.DeviceTokenRepository;

@Slf4j
@Component
public class ExpoNotificationPublisher implements NotificationPublisher {

  private final DeviceTokenRepository tokenRepository;
  private final ExpoPushNotificationClient expoClient;

  public ExpoNotificationPublisher(DeviceTokenRepository tokenRepository) {
    this.tokenRepository = tokenRepository;

    CloseableHttpClient httpClient = HttpClients.createDefault();

    this.expoClient = ExpoPushNotificationClient.builder().setHttpClient(httpClient).build();
  }

  @Override
  @Async
  public void sendMulticast(List<String> userIds, String title, String body,
      Map<String, Object> data) {

    List<String> tokens = tokenRepository.getTokensByUserIdIn(userIds);

    if (tokens.isEmpty()) {
      log.warn("No tokens found for users: {}", userIds);
      return;
    }

    List<PushNotification> notifications = new ArrayList<>();

    for (String token : tokens) {

      PushNotification push = new PushNotification();
      push.setTo(List.of(token));
      push.setTitle(title);
      push.setBody(body);

      push.setData(new HashMap<>(data));

      notifications.add(push);
    }

    try {
      List<TicketResponse.Ticket> tickets = expoClient.sendPushNotifications(notifications);

      for (TicketResponse.Ticket ticket : tickets) {
        log.info("Expo ticket: id={}, status={}", ticket.getId(), ticket.getStatus());
      }

    } catch (Exception e) {
      log.error("Error sending Expo notifications", e);
    }
  }
}
