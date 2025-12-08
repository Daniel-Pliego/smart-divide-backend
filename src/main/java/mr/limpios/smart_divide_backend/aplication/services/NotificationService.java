package mr.limpios.smart_divide_backend.aplication.services;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

import mr.limpios.smart_divide_backend.aplication.dtos.RegisterDeviceTokenDTO;
import mr.limpios.smart_divide_backend.aplication.repositories.DeviceTokenRepository;
import mr.limpios.smart_divide_backend.aplication.repositories.UserRepository;
import mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceNotFoundException;
import mr.limpios.smart_divide_backend.domain.models.Expense;
import mr.limpios.smart_divide_backend.domain.models.Group;
import mr.limpios.smart_divide_backend.domain.models.Payment;
import mr.limpios.smart_divide_backend.domain.models.User;

@Service
public class NotificationService {

  private final DeviceTokenRepository deviceTokenRepository;
  private final UserRepository userRepository;
  private final mr.limpios.smart_divide_backend.aplication.interfaces.NotificationPublisher notificationPublisher;

  public NotificationService(DeviceTokenRepository deviceTokenRepository,
      UserRepository userRepository,
      mr.limpios.smart_divide_backend.aplication.interfaces.NotificationPublisher notificationPublisher) {
    this.deviceTokenRepository = deviceTokenRepository;
    this.userRepository = userRepository;
    this.notificationPublisher = notificationPublisher;
  }

  public void registerToken(String userId, RegisterDeviceTokenDTO dto) {

    User user = userRepository.getUserbyId(userId);

    if (Objects.isNull(user)) {
      throw new ResourceNotFoundException(ExceptionsConstants.USER_NOT_FOUND);
    }

    if (dto.token() != null && !dto.token().isBlank()) {
      deviceTokenRepository.saveToken(user, dto.token());
    }
  }

  public void notifyExpenseCreated(User actor, Group group, Expense expense) {
    List<String> recipients = getGroupRecipients(group, actor);

    String title = "Nuevo Gasto";
    String body =
        String.format("%s agregó '%s' en %s", actor.name(), expense.description(), group.name());

    Map<String, Object> data = Map.of("type", "EXPENSE_CREATED", "groupId",
        String.valueOf(group.id()), "expenseId", String.valueOf(expense.id()));

    notificationPublisher.sendMulticast(recipients, title, body, data);
  }

  public void notifyExpenseDeleted(User actor, Group group, Expense expense) {
    List<String> recipients = getGroupRecipients(group, actor);

    String title = "Gasto Eliminado";
    String body =
        String.format("%s eliminó '%s' de %s", actor.name(), expense.description(), group.name());

    Map<String, Object> data = Map.of("type", "EXPENSE_DELETED", "groupId",
        String.valueOf(group.id()), "expenseId", String.valueOf(expense.id()));

    notificationPublisher.sendMulticast(recipients, title, body, data);
  }

  public void notifyMemberAdded(User actor, Group group, User newMember) {
    List<String> recipients = getGroupRecipients(group, actor);

    String title = "Nuevo Miembro";
    String body =
        String.format("%s añadió a %s al grupo %s", actor.name(), newMember.name(), group.name());

    Map<String, Object> data =
        Map.of("type", "MEMBER_ADDED", "groupId", String.valueOf(group.id()));

    notificationPublisher.sendMulticast(recipients, title, body, data);
  }

  public void notifyFriendshipRequest(User actor, User targetUser) {
    List<String> recipients = List.of(targetUser.id());

    String title = "Nueva Amistad";
    String body = String.format("%s quiere ser tu amigo", actor.name());

    Map<String, Object> data =
        Map.of("type", "FRIENDSHIP_REQUEST", "actorId", String.valueOf(actor.id()));

    notificationPublisher.sendMulticast(recipients, title, body, data);
  }

  public void notifyPayment(User payer, User receiver, Group group, Payment payment) {

    String title = "Pago Recibido";
    String body =
        String.format("%s te ha pagado $%s en %s", payer.name(), payment.amount(), group.name());

    Map<String, Object> data = Map.of("type", "PAYMENT_RECEIVED", "paymentId",
        String.valueOf(payment.id()), "groupId", String.valueOf(group.id()));

    notificationPublisher.sendMulticast(List.of(receiver.id()), title, body, data);
  }

  private List<String> getGroupRecipients(Group group, User actor) {
    return group.members().stream().map(User::id).filter(id -> !id.equals(actor.id())).toList();
  }
}
