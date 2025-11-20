package mr.limpios.smart_divide_backend.domain.dto;

import java.math.BigDecimal;

public record UserBalanceDTO(
        String userId,
        String name,
        BigDecimal balance
) {
}
