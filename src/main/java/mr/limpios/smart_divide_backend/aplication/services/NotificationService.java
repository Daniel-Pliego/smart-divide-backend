package mr.limpios.smart_divide_backend.aplication.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.niamedtech.expo.exposerversdk.ExpoPushNotificationClient;
import com.niamedtech.expo.exposerversdk.request.PushNotification;
import com.niamedtech.expo.exposerversdk.response.Status;
import com.niamedtech.expo.exposerversdk.response.TicketResponse;

import lombok.extern.slf4j.Slf4j;
import mr.limpios.smart_divide_backend.aplication.repositories.UserRepository;
import mr.limpios.smart_divide_backend.domain.dto.NotificationDTO;
import mr.limpios.smart_divide_backend.domain.models.User;

@Slf4j
@Service
public class NotificationService {

  @Autowired
  private UserRepository userRepository;

  private final ExpoPushNotificationClient expoPushClient;

  public NotificationService() {
    CloseableHttpClient httpClient = HttpClients.createDefault();
    this.expoPushClient = ExpoPushNotificationClient.builder().setHttpClient(httpClient).build();
  }

  public void sendPushNotificationToUser(String userId, NotificationDTO notification) {
    User user = userRepository.getUserbyId(userId);

    if (user == null || user.pushToken() == null || user.pushToken().isEmpty()) {
      log.debug("Usuario {} no tiene push token registrado", userId);
      return;
    }

    try {
      List<String> tokens = new ArrayList<>();
      tokens.add(user.pushToken());

      PushNotification pushNotification = new PushNotification();
      pushNotification.setTo(tokens);
      pushNotification.setTitle(notification.title());
      pushNotification.setBody(notification.message());

      if (notification.data() != null) {
        pushNotification.setData(notification.data());
      }

      pushNotification.setSound("default");

      List<PushNotification> notifications = new ArrayList<>();
      notifications.add(pushNotification);

      List<TicketResponse.Ticket> response = expoPushClient.sendPushNotifications(notifications);

      for (TicketResponse.Ticket ticket : response) {
        if (ticket.getStatus() == Status.ERROR) {
          log.error("Error enviando push: {}", ticket.getMessage());

          if (ticket.getMessage() != null && ticket.getMessage().contains("DeviceNotRegistered")) {

            User updatedUser = new User(user.id(), user.name(), user.lastName(), user.email(),
                user.password(), user.photoUrl(), user.isVerified(), user.cards(), null);

            userRepository.saveUser(updatedUser);
            log.info("Push token eliminado para usuario {}", userId);
          }
        } else {
          log.info("Push enviada exitosamente a usuario {}", userId);
        }
      }
    } catch (Exception e) {
      log.error("Error enviando push notification a usuario {}", userId, e);
    }
  }
}
