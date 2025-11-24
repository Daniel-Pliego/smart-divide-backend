package mr.limpios.smart_divide_backend.domain.dto;

import java.math.BigDecimal;

public record CreatePaymentDTO(
    String fromUserId,
    String toUserId,
    BigDecimal amount
) {
    
}
