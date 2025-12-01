package mr.limpios.smart_divide_backend.domain.dto.ExpenseDetails;

import java.util.List;

public record ExpenseBalanceDTO(
                ExpensePayerDetailDTO payer,
                List<ExpenseUserAmountDTO> debtors) {

}
