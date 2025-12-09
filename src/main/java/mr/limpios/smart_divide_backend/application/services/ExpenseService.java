package mr.limpios.smart_divide_backend.application.services;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.EXPENSE_NOT_FOUND;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.GROUP_NOT_FOUND;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import mr.limpios.smart_divide_backend.application.assemblers.ExpenseDetailAssembler;
import mr.limpios.smart_divide_backend.application.assemblers.ExpenseModelAssembler;
import mr.limpios.smart_divide_backend.application.dtos.ExpenseDetails.ExpenseDetailDTO;
import mr.limpios.smart_divide_backend.application.dtos.ExpenseInputDTO;
import mr.limpios.smart_divide_backend.application.dtos.ExpenseSummaryDTO;
import mr.limpios.smart_divide_backend.application.dtos.UserBalanceDTO;
import mr.limpios.smart_divide_backend.application.events.ExpenseCreatedEvent;
import mr.limpios.smart_divide_backend.application.repositories.ExpenseBalanceRepository;
import mr.limpios.smart_divide_backend.application.repositories.ExpenseGroupBalanceRepository;
import mr.limpios.smart_divide_backend.application.repositories.ExpenseRepository;
import mr.limpios.smart_divide_backend.application.repositories.GroupRepository;
import mr.limpios.smart_divide_backend.application.repositories.UserRepository;
import mr.limpios.smart_divide_backend.application.utils.CollectionUtils;
import mr.limpios.smart_divide_backend.application.validators.ExpenseValidator;
import mr.limpios.smart_divide_backend.application.validators.strategies.ExpenseValidationStrategyFactory;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceNotFoundException;
import mr.limpios.smart_divide_backend.domain.models.Expense;
import mr.limpios.smart_divide_backend.domain.models.ExpenseBalance;
import mr.limpios.smart_divide_backend.domain.models.ExpenseGroupBalance;
import mr.limpios.smart_divide_backend.domain.models.ExpenseParticipant;
import mr.limpios.smart_divide_backend.domain.models.Group;
import mr.limpios.smart_divide_backend.domain.models.User;

@Service
@AllArgsConstructor
public class ExpenseService {
  private final ExpenseRepository expenseRepository;
  private final GroupRepository groupRepository;
  private final ExpenseValidationStrategyFactory strategyFactory;
  private final ApplicationEventPublisher eventPublisher;
  private final ExpenseGroupBalanceRepository groupBalanceRepository;
  private final ExpenseBalanceRepository expenseBalanceRepository;
  private final ExpenseGroupBalanceService expenseGroupBalanceService;
  private final UserRepository userRepository;
  private final NotificationService notificationService;

  @Transactional
  public void addExpense(ExpenseInputDTO addExpenseDTO, String userId, String groupId) {
    Group group = groupRepository.getGroupById(groupId);
    if (Objects.isNull(group)) {
      throw new ResourceNotFoundException(GROUP_NOT_FOUND);
    }

    Map<String, User> membersMap = CollectionUtils.toMap(group.members(), User::id, user -> user);

    ExpenseValidator.validateGroupMembership(membersMap, userId, addExpenseDTO);

    strategyFactory.getStrategy(addExpenseDTO.divisionType()).validate(addExpenseDTO);

    List<ExpenseParticipant> ParticipantsPayersOfExpenses = ExpenseModelAssembler
        .createExpenseParticipantsFromValidatedParticipants(addExpenseDTO, membersMap);

    List<ExpenseBalance> balances = ExpenseModelAssembler
        .createExpenseBalanceFromValidatedParticipants(addExpenseDTO, membersMap);

    Expense expense = new Expense(null, addExpenseDTO.type(), addExpenseDTO.description(),
        BigDecimal.valueOf(addExpenseDTO.amount()), addExpenseDTO.evidenceUrl(), null,
        addExpenseDTO.divisionType(), group, ParticipantsPayersOfExpenses, balances);

    Expense savedExpense = this.expenseRepository.saveExpense(expense);
    eventPublisher.publishEvent(new ExpenseCreatedEvent(savedExpense));

    User actor = userRepository.getUserbyId(userId);
    notificationService.notifyExpenseCreated(actor, group, savedExpense);
  }

  public ExpenseDetailDTO getExpenseDetails(String expenseId, String userId, String groupId) {
    Expense expense = expenseRepository.findById(expenseId);
    if (Objects.isNull(expense)) {
      throw new ResourceNotFoundException(EXPENSE_NOT_FOUND);
    }

    ExpenseValidator.validateExpenseAccess(expense, userId, groupId);
    return ExpenseDetailAssembler.buildExpenseDetailDTO(expense);
  }

  public List<UserBalanceDTO> getUserBalancesByGroup(String groupId, String userId) {
    List<ExpenseGroupBalance> asCreditor =
        groupBalanceRepository.findByGroupIdAndCreditorId(groupId, userId);

    List<ExpenseGroupBalance> asDebtor =
        groupBalanceRepository.findByGroupIdAndDebtorId(groupId, userId);

    Stream<UserBalanceDTO> creditorStream =
        asCreditor.stream().map(balance -> new UserBalanceDTO(balance.debtor().id(),
            balance.debtor().name() + " " + balance.debtor().lastName(), balance.amount()));

    Stream<UserBalanceDTO> debtorStream = asDebtor.stream()
        .map(balance -> new UserBalanceDTO(balance.creditor().id(),
            balance.creditor().name() + " " + balance.creditor().lastName(),
            balance.amount().negate()));

    return Stream.concat(creditorStream, debtorStream)
        .collect(Collectors.groupingBy(UserBalanceDTO::userId,
            Collectors.reducing((dto1, dto2) -> new UserBalanceDTO(dto1.userId(), dto1.name(),
                dto1.balance().add(dto2.balance())))))
        .values().stream().filter(Optional::isPresent).map(Optional::get).toList();
  }

  public List<ExpenseSummaryDTO> getExpensesByGroup(String groupId,
      List<UserBalanceDTO> userBalances, String userId) {
    List<Expense> expenses = expenseRepository.findByGroupId(groupId);

    List<ExpenseSummaryDTO> result = new ArrayList<>();

    for (Expense expense : expenses) {
      result.add(ExpenseDetailAssembler.toExpenseSummaryDTO(expense, userId));
    }

    return result;
  }

  @Transactional
  public void deleteExpense(String expenseId, String userId) {

    Expense expenseToDelete = expenseRepository.findById(expenseId);
    if (Objects.isNull(expenseToDelete)) {
      throw new ResourceNotFoundException(EXPENSE_NOT_FOUND);
    }
    Group group = expenseToDelete.group();
    List<ExpenseBalance> transactionsToReverse =
        expenseBalanceRepository.findAllByExpenseId(expenseId);

    for (ExpenseBalance expenseBalance : transactionsToReverse) {

      expenseGroupBalanceService.applyReverseBalance(expenseBalance.creditor(),
          expenseBalance.debtor(), expenseBalance.amountToPaid(), group);
    }

    expenseRepository.deleteById(expenseId);

    User actor = userRepository.getUserbyId(userId);
    notificationService.notifyExpenseDeleted(actor, group, expenseToDelete);
  }

}
