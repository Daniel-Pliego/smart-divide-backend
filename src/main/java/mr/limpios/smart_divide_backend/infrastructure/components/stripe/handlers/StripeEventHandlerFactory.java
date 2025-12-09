package mr.limpios.smart_divide_backend.infrastructure.components.stripe.handlers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.stripe.model.Account;
import com.stripe.model.PaymentIntent;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class StripeEventHandlerFactory {

  private final AccountUpdatedHandler accountUpdatedHandler;
  private final PaymentIntentSucceededHandler paymentIntentSucceededHandler;

  public Map<String, StripeEventHandler> getHandlers() {
    Map<String, StripeEventHandler> handlers = new HashMap<>();
    handlers.put("account.updated",
        stripeObject -> accountUpdatedHandler.handle((Account) stripeObject));
    handlers.put("payment_intent.succeeded",
        stripeObject -> paymentIntentSucceededHandler.handle((PaymentIntent) stripeObject));
    return handlers;
  }
}
