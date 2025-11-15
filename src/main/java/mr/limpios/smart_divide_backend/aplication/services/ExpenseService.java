package mr.limpios.smart_divide_backend.aplication.services;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.DEBTORS_NOT_IN_GROUP;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.GROUP_NOT_FOUND;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.USER_NOT_MEMBER_OF_GROUP;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

import mr.limpios.smart_divide_backend.aplication.repositories.ExpenseGroupBalanceRepository;
import mr.limpios.smart_divide_backend.infraestructure.dto.UserBalanceDTO;
import mr.limpios.smart_divide_backend.domain.models.ExpenseGroupBalance;
import mr.limpios.smart_divide_backend.domain.models.BalanceInfo;
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
import mr.limpios.smart_divide_backend.infraestructure.dto.ExpenseDetailDTO;
import mr.limpios.smart_divide_backend.infraestructure.dto.ExpensePayerDTO;
import mr.limpios.smart_divide_backend.infraestructure.mappers.ExpenseMapper;

@Service
@AllArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final GroupRepository groupRepository;
    private final ExpenseStrategyFactory strategyFactory;
    private final ApplicationEventPublisher eventPublisher;
    private final ExpenseGroupBalanceRepository balanceRepository;

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
            throw new ResourceNotFoundException(USER_NOT_MEMBER_OF_GROUP);
        }

        HashSet<String> memberIds = new HashSet<>(membersMap.keySet());
        HashSet<String> dtoMemberIds = dto.balances().stream()
                .map(b -> b.debtorId())
                .collect(Collectors.toCollection(HashSet::new));

        if (!memberIds.equals(dtoMemberIds)) {
            throw new ResourceNotFoundException(DEBTORS_NOT_IN_GROUP);
        }
    }

    private Map<String, User> getMembersMap(Group group) {
        return group.members().stream()
                .collect(Collectors.toMap(User::id, member -> member));
    }

    public List<UserBalanceDTO> getUserBalancesByGroup(String groupId) {
        List<ExpenseGroupBalance> balances = balanceRepository.findByGroupId(groupId);

        Map<String, BalanceInfo> userBalanceMap = new HashMap<>();

        for (ExpenseGroupBalance balance : balances) {
            String creditorId = balance.creditor().id();
            userBalanceMap.putIfAbsent(creditorId, new BalanceInfo(
                    balance.creditor().name() + " " + balance.creditor().lastName(),
                    BigDecimal.ZERO
            ));
            userBalanceMap.get(creditorId).setBalance(userBalanceMap.get(creditorId).getBalance().add(balance.amount()));

            String debtorId = balance.debtor().id();
            userBalanceMap.putIfAbsent(debtorId, new BalanceInfo(
                    balance.debtor().name() + " " + balance.debtor().lastName(),
                    BigDecimal.ZERO
            ));
            userBalanceMap.get(debtorId).setBalance(userBalanceMap.get(debtorId).getBalance().subtract(balance.amount()));
        }

        List<UserBalanceDTO> result = new ArrayList<>();
        int index = 1;

        for (Map.Entry<String, BalanceInfo> entry : userBalanceMap.entrySet()) {
            result.add(new UserBalanceDTO(
                    index++,
                    entry.getKey(),
                    entry.getValue().getName(),
                    entry.getValue().getBalance()
            ));
        }

        return result;
    }

    public List<ExpenseDetailDTO> getExpensesByGroup(String groupId) {
        List<Expense> expenses = expenseRepository.findByGroupId(groupId);

        List<UserBalanceDTO> userBalances = getUserBalancesByGroup(groupId);

        Map<String, BigDecimal> balanceMap = userBalances.stream()
                .collect(Collectors.toMap(
                        UserBalanceDTO::userId,
                        UserBalanceDTO::balance
                ));

        int expenseIndex = 1;
        List<ExpenseDetailDTO> result = new ArrayList<>();

        for (Expense expense : expenses) {
            result.add(buildExpenseDetailDTO(expense, expenseIndex++, balanceMap));
        }

        return result;
    }

    private ExpenseDetailDTO buildExpenseDetailDTO(
            Expense expense,
            int expenseIndex,
            Map<String, BigDecimal> balanceMap
    ) {
        int payerIndex = 1;
        List<ExpensePayerDTO> payers = new ArrayList<>();

        for (ExpenseParticipant participant : expense.participants()) {
            payers.add(new ExpensePayerDTO(
                    payerIndex++,
                    participant.payer().id(),
                    participant.payer().name(),
                    participant.payer().lastName(),
                    participant.amountPaid(),
                    balanceMap.getOrDefault(
                            participant.payer().id(),
                            BigDecimal.ZERO
                    )
            ));
        }

        return new ExpenseDetailDTO(
                expenseIndex,
                expense.type(),
                expense.description(),
                expense.amount(),
                expense.createdAt(),
                payers
        );
    }

}
