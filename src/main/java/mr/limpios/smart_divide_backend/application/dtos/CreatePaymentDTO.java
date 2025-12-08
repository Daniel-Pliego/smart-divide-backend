package mr.limpios.smart_divide_backend.application.dtos;

import java.math.BigDecimal;

public record CreatePaymentDTO(
    String fromUserId,
    String toUserId,
    BigDecimal amount
) {
    
}
