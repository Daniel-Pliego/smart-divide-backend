package mr.limpios.smart_divide_backend.application.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ExpenseSummaryDTO(
        String id,
        String type,
        String description,
        BigDecimal amount,
        LocalDateTime createdAt,
        List<ExpensePayerDTO> payers,
        BigDecimal userBalance
) {}
