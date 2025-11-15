package mr.limpios.smart_divide_backend.infraestructure.dto;

import java.math.BigDecimal;

public record ExpensePayerDTO(
        Integer id,
        String userId,
        String name,
        String lastName,
        BigDecimal amountPaid,
        BigDecimal userBalance
) {}
