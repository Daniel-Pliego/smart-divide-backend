package mr.limpios.smart_divide_backend.aplication.services;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.GROUP_NOT_FOUND;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import mr.limpios.smart_divide_backend.aplication.repositories.ExpenseRepository;
import mr.limpios.smart_divide_backend.aplication.repositories.GroupRepository;
import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceNotFoundException;
import mr.limpios.smart_divide_backend.domain.models.DivisionType;
import mr.limpios.smart_divide_backend.domain.models.Expense;
import mr.limpios.smart_divide_backend.domain.models.ExpenseBalance;
import mr.limpios.smart_divide_backend.domain.models.ExpenseParticipant;
import mr.limpios.smart_divide_backend.domain.models.Group;
import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.infraestructure.dto.AddExpenseDTO;
import mr.limpios.smart_divide_backend.infraestructure.dto.AddExpenseDebtorsDTO;
import mr.limpios.smart_divide_backend.infraestructure.dto.ExpenseResumeDTO;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final GroupRepository groupRepository;

    public ExpenseService(ExpenseRepository expenseRepository, GroupRepository groupRepository) {
        this.expenseRepository = expenseRepository;
        this.groupRepository = groupRepository;
    }

    public ExpenseResumeDTO addExpense(AddExpenseDTO addExpenseDTO, String userId, String groupId) {
        Group group = groupRepository.getGroupById(groupId);
        if (Objects.isNull(group)) {
            throw new ResourceNotFoundException(GROUP_NOT_FOUND);
        }

        Map<String, User> groupMembersMap = group.members()
                .stream()
                .collect(Collectors.toMap(User::id, member -> member));

        HashSet<String> groupMemberIds = new HashSet<>(groupMembersMap.keySet());

        if (!groupMemberIds.contains(userId)) {
            throw new ResourceNotFoundException("User is not a member of the group");
        }

        addExpenseDTO.balances().forEach(balance -> {
            if (!groupMemberIds.contains(balance.debtorId())) {
                throw new ResourceNotFoundException("Member with ID " + balance.debtorId() + " not found in group");
            }
            groupMemberIds.remove(balance.debtorId());
        });

        if (groupMemberIds.size() > 1) {
            throw new InvalidDataException("At least one member was not included in the balances");
        }

        double debtorsDTOTotalAmount = addExpenseDTO.balances().stream()
                .map(balance -> balance.amountToPaid())
                .reduce(0d, Double::sum);

        switch (addExpenseDTO.divisionType()) {
            case DivisionType.EQUAL -> {
                double equalShare = addExpenseDTO.amount() / addExpenseDTO.balances().size();
                for (AddExpenseDebtorsDTO balance : addExpenseDTO.balances()) {
                    if (Double.compare(balance.amountToPaid(), equalShare) != 0) {
                        throw new InvalidDataException(
                                "For EQUAL division, each debtor must pay the same amount: " + equalShare);
                    }
                }
            }
            case DivisionType.CUSTOM -> {
                if (Double.compare(debtorsDTOTotalAmount, addExpenseDTO.amount()) != 0) {
                    throw new InvalidDataException(
                            "The sum of debtors amounts does not equal the total expense amount");
                }
            }
            case DivisionType.PERCENTAGE -> {
                double totalPercentage = addExpenseDTO.balances().stream()
                        .map(balance -> balance.amountToPaid())
                        .reduce(0d, Double::sum);
                if (Double.compare(totalPercentage, 100.0) != 0) {
                    throw new InvalidDataException("For PERCENTAGE division, the total percentage must equal 100%");
                }
            }
        }

        List<ExpenseParticipant> expenseParticipants = createParticipantsFromBalances(addExpenseDTO.balances(),
                groupMembersMap);
        List<ExpenseBalance> expenseBalances = createExpnseseBalancesFromBalances(addExpenseDTO.balances(),
                groupMembersMap, userId);

        Expense expenseResponse = this.expenseRepository.saveExpense(
                new Expense(
                        null,
                        addExpenseDTO.type(),
                        addExpenseDTO.description(),
                        BigDecimal.valueOf(addExpenseDTO.amount()),
                        addExpenseDTO.evidenUrl(),
                        null,
                        addExpenseDTO.divisionType(),
                        group,
                        expenseParticipants,
                        expenseBalances));

        return new ExpenseResumeDTO(
                expenseResponse.id(),
                expenseResponse.type(),
                expenseResponse.description(),
                expenseResponse.amount(),
                expenseResponse.createdAt());
    }

    private List<ExpenseParticipant> createParticipantsFromBalances(
            List<AddExpenseDebtorsDTO> balances,
            Map<String, User> groupMembersMap) {

        return balances.stream()
                .map(balance -> new ExpenseParticipant(
                        null,
                        groupMembersMap.get(balance.debtorId()),
                        BigDecimal.valueOf(0),
                        BigDecimal.valueOf(balance.amountToPaid())))
                .collect(Collectors.toList());
    }

    private List<ExpenseBalance> createExpnseseBalancesFromBalances(
            List<AddExpenseDebtorsDTO> balances,
            Map<String, User> groupMembersMap,
            String creditorId) {
        return balances.stream()
                .map(balance -> new ExpenseBalance(
                        null,
                        groupMembersMap.get(creditorId),
                        groupMembersMap.get(balance.debtorId()),
                        BigDecimal.valueOf(balance.amountToPaid())))
                .collect(Collectors.toList());
    }

}
