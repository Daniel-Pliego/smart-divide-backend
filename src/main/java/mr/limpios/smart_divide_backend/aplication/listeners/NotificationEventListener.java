package mr.limpios.smart_divide_backend.aplication.listeners;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.extern.slf4j.Slf4j;
import mr.limpios.smart_divide_backend.aplication.repositories.UserRepository;
import mr.limpios.smart_divide_backend.aplication.services.NotificationService;
import mr.limpios.smart_divide_backend.domain.dto.NotificationDTO;
import mr.limpios.smart_divide_backend.domain.events.ExpenseCreatedEvent;
import mr.limpios.smart_divide_backend.domain.events.FriendRequestCreatedEvent;
import mr.limpios.smart_divide_backend.domain.events.PaymentCreatedEvent;
import mr.limpios.smart_divide_backend.domain.events.UserAddedToGroupEvent;
import mr.limpios.smart_divide_backend.domain.models.*;

@Slf4j
@Component
public class NotificationEventListener {

  private final NotificationService notificationService;
  private final UserRepository userRepository;

  public NotificationEventListener(NotificationService notificationService,
      UserRepository userRepository) {
    this.notificationService = notificationService;
    this.userRepository = userRepository;
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleExpenseCreated(ExpenseCreatedEvent event) {
    Expense expense = event.expense();
    log.info("Handling ExpenseCreatedEvent for expense: {}", expense.id());

    String creatorId = null;
    String creatorName = "Alguien";

    for (ExpenseParticipant participant : expense.participants()) {
      if (participant.amountPaid() != null
          && participant.amountPaid().compareTo(BigDecimal.ZERO) > 0) {
        creatorId = participant.payer().id();
        creatorName = participant.payer().name() + " " + participant.payer().lastName();
        break;
      }
    }

    final String finalCreatorId = creatorId;
    final String finalCreatorName = creatorName;

    expense.participants().forEach(participant -> {
      if (!participant.payer().id().equals(finalCreatorId)) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", "NEW_EXPENSE");
        data.put("expenseId", expense.id());
        data.put("groupId", expense.group().id());

        NotificationDTO notification = new NotificationDTO("Nuevo gasto",
            finalCreatorName + " agregó: " + expense.description() + " - $" + expense.amount(),
            data);

        notificationService.sendPushNotificationToUser(participant.payer().id(), notification);
      }
    });
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handlePaymentCreated(PaymentCreatedEvent event) {
    Payment payment = event.payment();
    log.info("Handling PaymentCreatedEvent for payment: {}", payment.id());

    User payer = userRepository.getUserbyId(payment.fromUser().id());
    User recipient = userRepository.getUserbyId(payment.toUser().id());

    String payerName = payer != null ? payer.name() + " " + payer.lastName() : "Alguien";
    String recipientName =
        recipient != null ? recipient.name() + " " + recipient.lastName() : "Alguien";

    Map<String, Object> recipientData = new HashMap<>();
    recipientData.put("type", "PAYMENT_RECEIVED");
    recipientData.put("paymentId", payment.id());
    recipientData.put("amount", payment.amount());
    recipientData.put("groupId", payment.group().id());

    NotificationDTO recipientNotification = new NotificationDTO("Pago recibido",
        payerName + " te pagó $" + payment.amount(), recipientData);

    notificationService.sendPushNotificationToUser(payment.toUser().id(), recipientNotification);

    Map<String, Object> payerData = new HashMap<>();
    payerData.put("type", "PAYMENT_SENT");
    payerData.put("paymentId", payment.id());
    payerData.put("amount", payment.amount());

    NotificationDTO payerNotification = new NotificationDTO("Pago registrado",
        "Tu pago de $" + payment.amount() + " a " + recipientName + " fue registrado", payerData);

    notificationService.sendPushNotificationToUser(payment.fromUser().id(), payerNotification);
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleUserAddedToGroup(UserAddedToGroupEvent event) {
    log.info("Handling UserAddedToGroupEvent for user: {} in group: {}", event.addedUserId(),
        event.group().id());

    User adder = userRepository.getUserbyId(event.addedByUserId());
    String adderName = adder != null ? adder.name() + " " + adder.lastName() : "Alguien";

    Map<String, Object> data = new HashMap<>();
    data.put("type", "ADDED_TO_GROUP");
    data.put("groupId", event.group().id());

    NotificationDTO notification = new NotificationDTO("Nuevo grupo",
        adderName + " te agregó al grupo: " + event.group().name(), data);

    notificationService.sendPushNotificationToUser(event.addedUserId(), notification);
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleFriendRequestCreated(FriendRequestCreatedEvent event) {
    Friendship friendship = event.friendship();
    log.info("Handling FriendRequestCreatedEvent from: {} to: {}", friendship.requester().id(),
        friendship.friend().id());

    User requester = userRepository.getUserbyId(friendship.requester().id());
    NotificationDTO notification = getNotificationDTO(requester, friendship);

    notificationService.sendPushNotificationToUser(friendship.friend().id(), notification);
  }

  @NotNull
  private static NotificationDTO getNotificationDTO(User requester, Friendship friendship) {
    String requesterName =
        requester != null ? requester.name() + " " + requester.lastName() : "Alguien";

    Map<String, Object> data = new HashMap<>();
    data.put("type", "FRIEND_REQUEST");
    data.put("friendshipId", friendship.id());
    data.put("requesterId", friendship.requester().id());

    NotificationDTO notification = new NotificationDTO("Nueva solicitud de amistad",
        requesterName + " te envió una solicitud de amistad", data);
    return notification;
  }
}
