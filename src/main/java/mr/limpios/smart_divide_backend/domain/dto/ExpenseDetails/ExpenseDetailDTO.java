package mr.limpios.smart_divide_backend.domain.dto.ExpenseDetails;

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
        List<ExpenseBalanceDTO> balances) {
}
