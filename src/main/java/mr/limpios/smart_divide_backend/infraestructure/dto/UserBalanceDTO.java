package mr.limpios.smart_divide_backend.infraestructure.dto;

import java.math.BigDecimal;

public record UserBalanceDTO(
        Integer id,
        String userId,
        String name,
        BigDecimal balance
) {
}
