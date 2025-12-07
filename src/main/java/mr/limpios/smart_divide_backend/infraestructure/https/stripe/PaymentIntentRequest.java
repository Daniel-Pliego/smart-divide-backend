package mr.limpios.smart_divide_backend.infraestructure.https.stripe;

import java.math.BigDecimal;

public record PaymentIntentRequest(BigDecimal amount, String fromUser, String toUser) {

}
