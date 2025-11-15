package mr.limpios.smart_divide_backend.infraestructure.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentDetailDTO(
        Integer id,
        PaymentUserDTO fromUser,
        PaymentUserDTO toUser,
        BigDecimal amount,
        LocalDateTime createdAt
) {
}
