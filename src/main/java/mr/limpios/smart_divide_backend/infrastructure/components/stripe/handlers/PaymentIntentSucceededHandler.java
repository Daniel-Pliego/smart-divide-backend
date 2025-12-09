package mr.limpios.smart_divide_backend.infrastructure.components.stripe.handlers;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.stripe.model.PaymentIntent;

import lombok.AllArgsConstructor;
import mr.limpios.smart_divide_backend.application.dtos.CreatePaymentDTO;
import mr.limpios.smart_divide_backend.application.services.PaymentService;

@Component
@AllArgsConstructor
public class PaymentIntentSucceededHandler {

  private final PaymentService paymentService;

  public void handle(PaymentIntent paymentIntent) {
    Map<String, String> metadata = paymentIntent.getMetadata();
    String groupId = metadata.get("groupId");
    String fromUser = metadata.get("fromUser");
    String toUser = metadata.get("toUser");
    String amount = metadata.get("amount");

    paymentService.createPayment(fromUser, groupId,
        new CreatePaymentDTO(fromUser, toUser, new BigDecimal(amount)), true);
  }
}
