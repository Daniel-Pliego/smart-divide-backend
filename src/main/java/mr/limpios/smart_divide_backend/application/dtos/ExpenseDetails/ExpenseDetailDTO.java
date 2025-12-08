package mr.limpios.smart_divide_backend.application.dtos.ExpenseDetails;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ExpenseDetailDTO(
                String id,
                String type,
                String description,
                BigDecimal amount,
                LocalDateTime createdAt,
                String evidenceUrl,
                List<ExpenseUserAmountDTO> paidBy,
                List<ExpenseUserAmountDTO> distribution,
                List<ExpenseBalanceDTO> balances) {
}
