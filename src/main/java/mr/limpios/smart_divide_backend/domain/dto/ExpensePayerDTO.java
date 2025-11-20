package mr.limpios.smart_divide_backend.domain.dto;

import java.math.BigDecimal;

public record ExpensePayerDTO(
        String userId,
        String name,
        String lastName,
        BigDecimal amountPaid,
        BigDecimal userBalance
) {}
