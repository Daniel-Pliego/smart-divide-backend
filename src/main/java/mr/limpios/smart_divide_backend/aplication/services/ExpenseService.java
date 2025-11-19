package mr.limpios.smart_divide_backend.aplication.services;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.GROUP_NOT_FOUND;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import mr.limpios.smart_divide_backend.aplication.assemblers.ExpenseModelAssembler;
import mr.limpios.smart_divide_backend.aplication.utils.CollectionUtils;
import mr.limpios.smart_divide_backend.domain.validators.ExpenseValidator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import mr.limpios.smart_divide_backend.aplication.repositories.ExpenseRepository;
import mr.limpios.smart_divide_backend.aplication.repositories.GroupRepository;
import mr.limpios.smart_divide_backend.domain.events.ExpenseCreatedEvent;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceNotFoundException;
import mr.limpios.smart_divide_backend.domain.models.Expense;
import mr.limpios.smart_divide_backend.domain.models.ExpenseBalance;
import mr.limpios.smart_divide_backend.domain.models.ExpenseParticipant;
import mr.limpios.smart_divide_backend.domain.models.Group;
import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.domain.validators.strategies.ExpenseValidationStrategyFactory;
import mr.limpios.smart_divide_backend.domain.dto.ExpenseInputDTO;

@Service
@AllArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final GroupRepository groupRepository;
    private final ExpenseValidationStrategyFactory strategyFactory;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void addExpense(ExpenseInputDTO addExpenseDTO, String userId, String groupId) {
        Group group = groupRepository.getGroupById(groupId);
        if (Objects.isNull(group)) {
            throw new ResourceNotFoundException(GROUP_NOT_FOUND);
        }

        Map<String, User> membersMap = CollectionUtils.toMap(
                group.members(),
                User::id,
                user -> user
        );

        ExpenseValidator.validateGroupMembership(membersMap, userId, addExpenseDTO);

        strategyFactory
                .getStrategy(addExpenseDTO.divisionType())
                .validate(addExpenseDTO);

        List<ExpenseParticipant> ParticipantsPayersOfExpenses = ExpenseModelAssembler.createExpenseParticipantsFromValidatedParticipants(
                addExpenseDTO,
                membersMap);

        List<ExpenseBalance> balances = ExpenseModelAssembler.createExpenseBalanceFromValidatedParticipants(
                addExpenseDTO,
                membersMap);

        Expense expense = new Expense(
                null,
                addExpenseDTO.type(),
                addExpenseDTO.description(),
                BigDecimal.valueOf(addExpenseDTO.amount()),
                addExpenseDTO.evidenceUrl(),
                null,
                addExpenseDTO.divisionType(),
                group,
                ParticipantsPayersOfExpenses,
                balances);

        Expense savedExpense = this.expenseRepository.saveExpense(expense);
        eventPublisher.publishEvent(new ExpenseCreatedEvent(savedExpense));
    }
}
