package mr.limpios.smart_divide_backend.application.dtos;

import java.math.BigDecimal;

public record ExpensePayerDTO(
        String userId,
        String name,
        String lastName,
        BigDecimal amountPaid
) {}
