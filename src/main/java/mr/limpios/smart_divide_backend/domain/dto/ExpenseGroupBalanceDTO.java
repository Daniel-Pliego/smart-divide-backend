package mr.limpios.smart_divide_backend.domain.dto;

import java.util.List;

public record ExpenseGroupBalanceDTO(
        String userId,
        List<BalanceDetailDTO> creditBalance,
        List<BalanceDetailDTO> debtBalance) {

}
