package mr.limpios.smart_divide_backend.application.dtos;

import java.math.BigDecimal;

public record UserBalanceDTO(
        String userId,
        String name,
        BigDecimal balance
) {
}
