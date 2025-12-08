package mr.limpios.smart_divide_backend.aplication.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.GROUP_NOT_FOUND;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.NO_EXISTING_DEBTS_FOR_USER_PAIR;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.PAYMENT_AMOUNT_EXCEEDS_DEBT;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.USER_NOT_FOUND;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.instancio.Instancio;
import static org.instancio.Select.field;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import mr.limpios.smart_divide_backend.aplication.repositories.ExpenseGroupBalanceRepository;
import mr.limpios.smart_divide_backend.aplication.repositories.GroupRepository;
import mr.limpios.smart_divide_backend.aplication.repositories.PaymentRepository;
import mr.limpios.smart_divide_backend.aplication.repositories.UserRepository;
import mr.limpios.smart_divide_backend.domain.dto.CreatePaymentDTO;
import mr.limpios.smart_divide_backend.domain.dto.PaymentDetailDTO;
import mr.limpios.smart_divide_backend.domain.events.PaymentCreatedEvent;
import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceNotFoundException;
import mr.limpios.smart_divide_backend.domain.models.ExpenseGroupBalance;
import mr.limpios.smart_divide_backend.domain.models.Group;
import mr.limpios.smart_divide_backend.domain.models.Payment;
import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.domain.validators.PaymentValidator;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private ExpenseGroupBalanceRepository balanceRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    @DisplayName("Create Payment - Success: Valid data, debt exists and amount is correct")
    public void createPayment_success() {
        try (MockedStatic<PaymentValidator> mockedValidator = Mockito.mockStatic(PaymentValidator.class)) {

            String groupId = "group-1";
            String fromUserId = "user-payer";
            String toUserId = "user-receiver";
            BigDecimal debtAmount = new BigDecimal("100.00");
            BigDecimal paymentAmount = new BigDecimal("50.00");

            Group group = Instancio.of(Group.class).set(field("id"), groupId).create();
            User fromUser = Instancio.of(User.class).set(field("id"), fromUserId).create();
            User toUser = Instancio.of(User.class).set(field("id"), toUserId).create();

            CreatePaymentDTO paymentDTO = new CreatePaymentDTO(fromUserId, toUserId, paymentAmount);

            ExpenseGroupBalance existingBalance = new ExpenseGroupBalance(
                    1, toUser, fromUser, debtAmount, group);

            when(groupRepository.getGroupById(groupId)).thenReturn(group);
            when(userRepository.getUserbyId(fromUserId)).thenReturn(fromUser);
            when(userRepository.getUserbyId(toUserId)).thenReturn(toUser);

            when(balanceRepository.findByCreditorAndDebtorAndGroup(toUserId, fromUserId, groupId))
                    .thenReturn(Optional.of(existingBalance));

            Payment savedPayment = new Payment("test-id", fromUser, toUser, paymentAmount, group, false,
                    LocalDateTime.now());
            when(paymentRepository.savePayment(any(Payment.class))).thenReturn(savedPayment);

            paymentService.createPayment(fromUserId, groupId, paymentDTO, false);

            ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
            verify(paymentRepository).savePayment(paymentCaptor.capture());
            Payment capturedPayment = paymentCaptor.getValue();

            assertEquals(paymentAmount, capturedPayment.amount());
            assertEquals(fromUserId, capturedPayment.fromUser().id());
            assertEquals(toUserId, capturedPayment.toUser().id());

            verify(eventPublisher).publishEvent(any(PaymentCreatedEvent.class));
        }
    }

    @Test
    @DisplayName("Create Payment - Group Not Found: Throws ResourceNotFoundException")
    public void createPayment_groupNotFound_throwsException() {
        String groupId = "non-existent-group";
        String userId = "user-1";
        CreatePaymentDTO dto = Instancio.create(CreatePaymentDTO.class);

        when(groupRepository.getGroupById(groupId)).thenReturn(null);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> paymentService.createPayment(userId, groupId, dto, false));

        assertEquals(GROUP_NOT_FOUND, ex.getMessage());
        verify(paymentRepository, never()).savePayment(any());
    }

    @Test
    @DisplayName("Create Payment - User Not Found: Throws ResourceNotFoundException")
    public void createPayment_userNotFound_throwsException() {
        try (MockedStatic<PaymentValidator> mockedValidator = Mockito.mockStatic(PaymentValidator.class)) {
            String groupId = "group-1";
            Group group = Instancio.of(Group.class).set(field("id"), groupId).create();
            CreatePaymentDTO dto = new CreatePaymentDTO("missing-user", "user-2", BigDecimal.TEN);

            when(groupRepository.getGroupById(groupId)).thenReturn(group);
            when(userRepository.getUserbyId("missing-user")).thenReturn(null);

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                    () -> paymentService.createPayment("missing-user", groupId, dto, false));

            assertEquals(USER_NOT_FOUND, ex.getMessage());
        }
    }

    @Test
    @DisplayName("Create Payment - User Not Found: Throws ResourceNotFoundException")
    public void createPayment_userNotFound_throwsException2() {
        try (MockedStatic<PaymentValidator> mockedValidator = Mockito.mockStatic(PaymentValidator.class)) {
            String groupId = "group-1";
            Group group = Instancio.of(Group.class).set(field("id"), groupId).create();
            CreatePaymentDTO dto = new CreatePaymentDTO("missing-user", "user-2", BigDecimal.TEN);

            when(groupRepository.getGroupById(groupId)).thenReturn(group);
            when(userRepository.getUserbyId("user-2")).thenReturn(null);
            when(userRepository.getUserbyId("missing-user")).thenReturn(Instancio.create(User.class));

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                    () -> paymentService.createPayment("missing-user", groupId, dto, false));

            assertEquals(USER_NOT_FOUND, ex.getMessage());
        }
    }

    @Test
    @DisplayName("Create Payment - No Debt Exists: Throws InvalidDataException")
    public void createPayment_noDebtExists_throwsException() {
        try (MockedStatic<PaymentValidator> mockedValidator = Mockito.mockStatic(PaymentValidator.class)) {
            String groupId = "group-1";
            String fromId = "user-1";
            String toId = "user-2";

            Group group = Instancio.of(Group.class).set(field("id"), groupId).create();
            User fromUser = Instancio.of(User.class).set(field("id"), fromId).create();
            User toUser = Instancio.of(User.class).set(field("id"), toId).create();
            CreatePaymentDTO dto = new CreatePaymentDTO(fromId, toId, BigDecimal.TEN);

            when(groupRepository.getGroupById(groupId)).thenReturn(group);
            when(userRepository.getUserbyId(fromId)).thenReturn(fromUser);
            when(userRepository.getUserbyId(toId)).thenReturn(toUser);

            when(balanceRepository.findByCreditorAndDebtorAndGroup(toId, fromId, groupId))
                    .thenReturn(Optional.empty());

            InvalidDataException ex = assertThrows(InvalidDataException.class,
                    () -> paymentService.createPayment(fromId, groupId, dto, false));

            assertEquals(NO_EXISTING_DEBTS_FOR_USER_PAIR, ex.getMessage());
        }
    }

    @Test
    @DisplayName("Create Payment - Payment Exceeds Debt: Throws InvalidDataException")
    public void createPayment_amountExceedsDebt_throwsException() {
        try (MockedStatic<PaymentValidator> mockedValidator = Mockito.mockStatic(PaymentValidator.class)) {
            String groupId = "group-1";
            String fromId = "user-1";
            String toId = "user-2";

            Group group = Instancio.of(Group.class).set(field("id"), groupId).create();
            User fromUser = Instancio.of(User.class).set(field("id"), fromId).create();
            User toUser = Instancio.of(User.class).set(field("id"), toId).create();

            CreatePaymentDTO dto = new CreatePaymentDTO(fromId, toId, new BigDecimal("100.00"));

            when(groupRepository.getGroupById(groupId)).thenReturn(group);
            when(userRepository.getUserbyId(fromId)).thenReturn(fromUser);
            when(userRepository.getUserbyId(toId)).thenReturn(toUser);

            ExpenseGroupBalance balance = new ExpenseGroupBalance(
                    1, toUser, fromUser, new BigDecimal("50.00"), group);

            when(balanceRepository.findByCreditorAndDebtorAndGroup(toId, fromId, groupId))
                    .thenReturn(Optional.of(balance));

            InvalidDataException ex = assertThrows(InvalidDataException.class,
                    () -> paymentService.createPayment(fromId, groupId, dto, false));

            assertEquals(PAYMENT_AMOUNT_EXCEEDS_DEBT, ex.getMessage());
            verify(paymentRepository, never()).savePayment(any());
        }
    }

    @Test
    @DisplayName("Get Payments by Group - Success")
    public void getPaymentsByGroup_success() {
        String groupId = "group-1";

        List<Payment> payments = Instancio.ofList(Payment.class)
                .size(3)
                .supply(field(Payment.class, "group"),
                        () -> Instancio.of(Group.class).set(field("id"), groupId).create())
                .create();

        when(paymentRepository.findByGroupId(groupId)).thenReturn(payments);

        List<PaymentDetailDTO> result = paymentService.getPaymentsByGroup(groupId);

        assertNotNull(result);
        assertEquals(3, result.size());

        assertEquals(payments.get(0).id(), result.get(0).id());
        assertEquals(payments.get(0).amount(), result.get(0).amount());
        assertEquals(payments.get(0).fromUser().name(), result.get(0).fromUser().name());
    }

    @Test
    @DisplayName("Get Payments by Group - Empty List")
    public void getPaymentsByGroup_emptyList() {
        String groupId = "group-empty";
        when(paymentRepository.findByGroupId(groupId)).thenReturn(List.of());

        List<PaymentDetailDTO> result = paymentService.getPaymentsByGroup(groupId);

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}