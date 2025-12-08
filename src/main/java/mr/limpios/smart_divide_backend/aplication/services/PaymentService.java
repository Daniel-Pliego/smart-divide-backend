package mr.limpios.smart_divide_backend.aplication.services;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.GROUP_NOT_FOUND;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.NO_EXISTING_DEBTS_FOR_USER_PAIR;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.PAYMENT_AMOUNT_EXCEEDS_DEBT;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.USER_NOT_FOUND;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import mr.limpios.smart_divide_backend.aplication.repositories.ExpenseGroupBalanceRepository;
import mr.limpios.smart_divide_backend.aplication.repositories.GroupRepository;
import mr.limpios.smart_divide_backend.aplication.repositories.PaymentRepository;
import mr.limpios.smart_divide_backend.aplication.repositories.UserRepository;
import mr.limpios.smart_divide_backend.domain.dto.CreatePaymentDTO;
import mr.limpios.smart_divide_backend.domain.dto.PaymentDetailDTO;
import mr.limpios.smart_divide_backend.domain.dto.PaymentUserDTO;
import mr.limpios.smart_divide_backend.domain.events.PaymentCreatedEvent;
import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceNotFoundException;
import mr.limpios.smart_divide_backend.domain.models.ExpenseGroupBalance;
import mr.limpios.smart_divide_backend.domain.models.Group;
import mr.limpios.smart_divide_backend.domain.models.Payment;
import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.domain.validators.PaymentValidator;

@Service
@AllArgsConstructor
public class PaymentService {

  private final PaymentRepository paymentRepository;
  private final UserRepository userRepository;
  private final GroupRepository groupRepository;
  private final ExpenseGroupBalanceRepository balanceRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final NotificationService notificationService;

  public List<PaymentDetailDTO> getPaymentsByGroup(String groupId) {
    List<Payment> payments = paymentRepository.findByGroupId(groupId);

    return payments.stream().map(this::buildPaymentDetailDTO).collect(Collectors.toList());
  }

  @Transactional
  public void createPayment(String userId, String groupId, CreatePaymentDTO createPaymentDTO,
      Boolean paidByCard) {
    Group group = groupRepository.getGroupById(groupId);
    if (Objects.isNull(group)) {
      throw new ResourceNotFoundException(GROUP_NOT_FOUND);
    }

    PaymentValidator.validate(createPaymentDTO, userId, group);

    User fromUser = userRepository.getUserbyId(createPaymentDTO.fromUserId());
    if (Objects.isNull(fromUser)) {
      throw new ResourceNotFoundException(USER_NOT_FOUND);
    }
    User toUser = userRepository.getUserbyId(createPaymentDTO.toUserId());
    if (Objects.isNull(toUser)) {
      throw new ResourceNotFoundException(USER_NOT_FOUND);
    }

    Optional<ExpenseGroupBalance> existingBalance =
        balanceRepository.findByCreditorAndDebtorAndGroup(toUser.id(), fromUser.id(), groupId);
    if (existingBalance.isEmpty()) {
      throw new InvalidDataException(NO_EXISTING_DEBTS_FOR_USER_PAIR);
    }
    ExpenseGroupBalance balance = existingBalance.get();

    if (createPaymentDTO.amount().compareTo(balance.amount()) > 0) {
      throw new InvalidDataException(PAYMENT_AMOUNT_EXCEEDS_DEBT);
    }

    Payment payment = new Payment(null, fromUser, toUser, createPaymentDTO.amount(), group,
        paidByCard, LocalDateTime.now());
    Payment savedPayment = paymentRepository.savePayment(payment);

    eventPublisher.publishEvent(new PaymentCreatedEvent(savedPayment, balance));
    notificationService.notifyPayment(fromUser, toUser, group, savedPayment);
  }

  private PaymentDetailDTO buildPaymentDetailDTO(Payment payment) {

    PaymentUserDTO fromUser =
        new PaymentUserDTO(payment.fromUser().name(), payment.fromUser().lastName());

    PaymentUserDTO toUser =
        new PaymentUserDTO(payment.toUser().name(), payment.toUser().lastName());

    return new PaymentDetailDTO(payment.id(), fromUser, toUser, payment.amount(),
        payment.createdAt());
  }
}
