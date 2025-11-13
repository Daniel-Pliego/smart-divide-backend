package mr.limpios.smart_divide_backend.aplication.services;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.GROUP_NOT_FOUND;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
import mr.limpios.smart_divide_backend.domain.strategies.CalculatedBalance;
import mr.limpios.smart_divide_backend.domain.strategies.ExpenseStrategyFactory;
import mr.limpios.smart_divide_backend.infraestructure.dto.ExpenseInputDTO;
import mr.limpios.smart_divide_backend.infraestructure.dto.ExpenseResumeDTO;
import mr.limpios.smart_divide_backend.infraestructure.mappers.ExpenseMapper;

@Service
@AllArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final GroupRepository groupRepository;
    private final ExpenseStrategyFactory strategyFactory;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ExpenseResumeDTO addExpense(ExpenseInputDTO addExpenseDTO, String userId, String groupId) {
        Group group = groupRepository.getGroupById(groupId);
        if (Objects.isNull(group)) {
            throw new ResourceNotFoundException(GROUP_NOT_FOUND);
        }

        Map<String, User> membersMap = getMembersMap(group);

        validateGroupMembership(membersMap, userId, addExpenseDTO);

        List<CalculatedBalance> calculatedBalances = strategyFactory
                .getStrategy(addExpenseDTO.divisionType())
                .calculate(addExpenseDTO);

        List<ExpenseParticipant> participants = ExpenseMapper.createParticipantsFromBalances(
                calculatedBalances,
                membersMap);
        List<ExpenseBalance> balances = ExpenseMapper.createExpenseBalancesFromBalances(
                calculatedBalances,
                membersMap, userId);

        Expense expense = ExpenseMapper.toEntity(addExpenseDTO, group, participants, balances);
        Expense savedExpense = this.expenseRepository.saveExpense(expense);

        eventPublisher.publishEvent(new ExpenseCreatedEvent(savedExpense));

        return ExpenseMapper.toResumeDTO(savedExpense);
    }

    private void validateGroupMembership(Map<String, User> membersMap, String userId, ExpenseInputDTO dto) {
        if (!membersMap.containsKey(userId)) {
            throw new ResourceNotFoundException("User is not a member of the group");
        }

        HashSet<String> memberIds = new HashSet<>(membersMap.keySet());
        HashSet<String> dtoMemberIds = dto.balances().stream()
                .map(b -> b.debtorId())
                .collect(Collectors.toCollection(HashSet::new));

        if (!memberIds.equals(dtoMemberIds)) {
            throw new ResourceNotFoundException("One or more debtors are not in the group");
        }
    }

    private Map<String, User> getMembersMap(Group group) {
        return group.members().stream()
                .collect(Collectors.toMap(User::id, member -> member));
    }
}
