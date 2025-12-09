package mr.limpios.smart_divide_backend.infrastructure.dtos.stripe;

import java.math.BigDecimal;

public record PaymentIntentRequest(BigDecimal amount, String fromUser, String toUser) {

}
