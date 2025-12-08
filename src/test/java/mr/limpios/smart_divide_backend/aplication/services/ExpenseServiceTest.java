package mr.limpios.smart_divide_backend.application.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.GROUP_NOT_FOUND;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.instancio.Instancio;
import static org.instancio.Select.field;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import mr.limpios.smart_divide_backend.application.assemblers.ExpenseModelAssembler;
import mr.limpios.smart_divide_backend.application.repositories.ExpenseGroupBalanceRepository;
import mr.limpios.smart_divide_backend.application.repositories.ExpenseRepository;
import mr.limpios.smart_divide_backend.application.repositories.GroupRepository;
import mr.limpios.smart_divide_backend.application.repositories.UserRepository;
import mr.limpios.smart_divide_backend.application.dtos.ExpenseInputDTO;
import mr.limpios.smart_divide_backend.application.dtos.ExpenseSummaryDTO;
import mr.limpios.smart_divide_backend.application.dtos.UserBalanceDTO;
import mr.limpios.smart_divide_backend.application.events.ExpenseCreatedEvent;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceNotFoundException;
import mr.limpios.smart_divide_backend.domain.models.Expense;
import mr.limpios.smart_divide_backend.domain.models.ExpenseGroupBalance;
import mr.limpios.smart_divide_backend.domain.models.ExpenseParticipant;
import mr.limpios.smart_divide_backend.domain.models.Group;
import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.application.validators.ExpenseValidator;
import mr.limpios.smart_divide_backend.application.validators.strategies.AbstractExpenseValidationStrategy;
import mr.limpios.smart_divide_backend.application.validators.strategies.ExpenseValidationStrategyFactory;

@ExtendWith(MockitoExtension.class)
public class ExpenseServiceTest {

        @Mock
        private ExpenseRepository expenseRepository;
        @Mock
        private GroupRepository groupRepository;
        @Mock
        private ExpenseValidationStrategyFactory strategyFactory;
        @Mock
        private ApplicationEventPublisher eventPublisher;
        @Mock
        private ExpenseGroupBalanceRepository balanceRepository;

        @Mock
        private UserRepository userRepository;

        @Mock
        private NotificationService notificationService;

        @InjectMocks
        private ExpenseService expenseService;

        // --- TESTS PARA addExpense ---

        @Test
        @DisplayName("Add Expense - Success: Valid group and input triggers save and event")
        public void addExpense_success() {
                // Necesitamos mockear varios métodos estáticos simultáneamente
                try (MockedStatic<ExpenseValidator> validatorMock = Mockito.mockStatic(ExpenseValidator.class);
                                MockedStatic<ExpenseModelAssembler> assemblerMock = Mockito
                                                .mockStatic(ExpenseModelAssembler.class)) {

                        // Arrange
                        String groupId = "group-1";
                        String userId = "user-1";
                        Group group = Instancio.of(Group.class).set(field("id"), groupId).create();
                        ExpenseInputDTO inputDTO = Instancio.create(ExpenseInputDTO.class);

                        when(groupRepository.getGroupById(groupId)).thenReturn(group);

                        // Mockear la Strategy Factory
                        AbstractExpenseValidationStrategy strategyMock = mock(AbstractExpenseValidationStrategy.class);
                        when(strategyFactory.getStrategy(inputDTO.divisionType())).thenReturn(strategyMock);

                        // Mockear el Assembler para que devuelva listas vacías (o pobladas si se
                        // requiriera validación profunda)
                        // Esto evita NullPointerException en el constructor de Expense
                        when(ExpenseModelAssembler.createExpenseParticipantsFromValidatedParticipants(any(), anyMap()))
                                        .thenReturn(new ArrayList<>());
                        when(ExpenseModelAssembler.createExpenseBalanceFromValidatedParticipants(any(), anyMap()))
                                        .thenReturn(new ArrayList<>());

                        // Mockear el guardado
                        Expense savedExpense = Instancio.create(Expense.class);
                        when(expenseRepository.saveExpense(any(Expense.class))).thenReturn(savedExpense);

                        // Act
                        expenseService.addExpense(inputDTO, userId, groupId);

                        // Assert
                        // 1. Verificar que se validó la membresía (Static)
                        validatorMock.verify(() -> ExpenseValidator.validateGroupMembership(anyMap(), eq(userId),
                                        eq(inputDTO)));

                        // 2. Verificar que se ejecutó la estrategia de validación
                        verify(strategyMock).validate(inputDTO);

                        // 3. Verificar guardado y evento
                        verify(expenseRepository).saveExpense(any(Expense.class));
                        verify(eventPublisher).publishEvent(any(ExpenseCreatedEvent.class));
                }
        }

        @Test
        @DisplayName("Add Expense - Group Not Found: Throws ResourceNotFoundException")
        public void addExpense_groupNotFound_throwsException() {
                // Arrange
                String groupId = "missing-group";
                when(groupRepository.getGroupById(groupId)).thenReturn(null);

                // Act & Assert
                ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                                () -> expenseService.addExpense(Instancio.create(ExpenseInputDTO.class), "user-1",
                                                groupId));

                assertEquals(GROUP_NOT_FOUND, ex.getMessage());
                verify(expenseRepository, never()).saveExpense(any());
        }

        // --- TESTS PARA getUserBalancesByGroup ---

        @Test
        @DisplayName("Get User Balances - Success: Calculates positive and negative balances correctly")
        public void getUserBalancesByGroup_success() {
                // Arrange
                String groupId = "group-1";
                String userId = "target-user";

                User targetUser = Instancio.of(User.class).set(field("id"), userId).create();
                User otherUser = Instancio.of(User.class).create(); // Random ID

                // Escenario:
                // 1. TargetUser es ACREEDOR (le deben) 100.00 -> Positivo
                ExpenseGroupBalance creditorBalance = Instancio.of(ExpenseGroupBalance.class)
                                .set(field("creditor"), targetUser)
                                .set(field("debtor"), otherUser)
                                .set(field("amount"), new BigDecimal("100.00"))
                                .create();

                // 2. TargetUser es DEUDOR (debe) 40.00 -> Negativo
                ExpenseGroupBalance debtorBalance = Instancio.of(ExpenseGroupBalance.class)
                                .set(field("creditor"), otherUser)
                                .set(field("debtor"), targetUser)
                                .set(field("amount"), new BigDecimal("40.00"))
                                .create();

                when(balanceRepository.findByGroupIdAndCreditorId(groupId, userId))
                                .thenReturn(List.of(creditorBalance));
                when(balanceRepository.findByGroupIdAndDebtorId(groupId, userId))
                                .thenReturn(List.of(debtorBalance));

                // Act
                List<UserBalanceDTO> result = expenseService.getUserBalancesByGroup(groupId, userId);

                // Assert
                // Esperamos 2 resultados porque se agrupan por UserID de la contraparte.
                // Resultado 1: "otherUser" debe 100 a target (Positive)
                // Resultado 2: "otherUser" recibe 40 de target (Negative para target)
                // NOTA: El código agrupa por `userId`.
                // En creditorStream: UserBalanceDTO(debtor.id, debtor.name, amount) ->
                // (otherUser, 100)
                // En debtorStream: UserBalanceDTO(creditor.id, creditor.name, -amount) ->
                // (otherUser, -40)

                // Al agrupar por el ID de "otherUser", se suman: 100 + (-40) = 60.

                assertEquals(1, result.size());
                UserBalanceDTO dto = result.get(0);

                assertEquals(otherUser.id(), dto.userId()); // El balance es RELATIVO a este usuario
                assertEquals(new BigDecimal("60.00"), dto.balance());
        }

        // --- TESTS PARA getExpensesByGroup ---

        @Test
        @DisplayName("Get Expenses by Group - Success: Mails mapping and user specific balance calculation")
        public void getExpensesByGroup_success() {
                // Arrange
                String groupId = "group-1";
                String userId = "my-user-id";

                // Setup User Balances (Input dummy)
                List<UserBalanceDTO> userBalances = Collections.emptyList();

                // Setup Expense with Participants
                // ExpenseParticipant necesita tener el campo 'payer' con el ID del usuario para
                // probar la lógica de "userBalance"
                User me = Instancio.of(User.class).set(field("id"), userId).create();

                ExpenseParticipant participantMe = Instancio.of(ExpenseParticipant.class)
                                .set(field("payer"), me)
                                .set(field("amountPaid"), new BigDecimal("200.00")) // Pagué 200
                                .set(field("mustPaid"), new BigDecimal("50.00")) // Debía pagar 50
                                .create(); // Balance en este gasto: +150

                Expense expense = Instancio.of(Expense.class)
                                .set(field("participants"), List.of(participantMe))
                                .create();

                when(expenseRepository.findByGroupId(groupId)).thenReturn(List.of(expense));

                // Act
                List<ExpenseSummaryDTO> result = expenseService.getExpensesByGroup(groupId, userBalances, userId);

                // Assert
                assertNotNull(result);
                assertEquals(1, result.size());
                ExpenseSummaryDTO detail = result.get(0);

                assertEquals(expense.id(), detail.id());

                // Validar lógica de negocio del método privado buildExpenseDetailDTO
                // userBalance = amountPaid (200) - mustPaid (50) = 150
                assertEquals(new BigDecimal("150.00"), detail.userBalance());
        }
}