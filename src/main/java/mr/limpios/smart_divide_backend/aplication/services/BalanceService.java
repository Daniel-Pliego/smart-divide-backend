package mr.limpios.smart_divide_backend.aplication.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import mr.limpios.smart_divide_backend.aplication.repositories.ExpenseGroupBalanceRepository;
import mr.limpios.smart_divide_backend.domain.dto.BalanceDetailDTO;
import mr.limpios.smart_divide_backend.domain.dto.ExpenseGroupBalanceDTO;
import mr.limpios.smart_divide_backend.domain.models.ExpenseGroupBalance;

@Service
@AllArgsConstructor
public class BalanceService {

    private ExpenseGroupBalanceRepository expenseGroupBalanceRepository;

    public ExpenseGroupBalanceDTO getBalanceByUserAndGroup(String userId, String groupId) {

        List<ExpenseGroupBalance> participantBalance = expenseGroupBalanceRepository
                .findByGroupIdAndParticipant(groupId, userId);

        Map<Boolean, List<BalanceDetailDTO>> balancesPartitioned = participantBalance.stream()
                .collect(Collectors.partitioningBy(
                        balance -> balance.creditor().id().equals(userId),
                        Collectors.mapping(this::mapToDetailDTO, Collectors.toList())
                ));

        return new ExpenseGroupBalanceDTO(
                userId,
                balancesPartitioned.get(true),
                balancesPartitioned.get(false));
    }

    private BalanceDetailDTO mapToDetailDTO(ExpenseGroupBalance balance) {
        return new BalanceDetailDTO(
                balance.creditor().id(),
                balance.debtor().id(),
                balance.amount());
    }

}
