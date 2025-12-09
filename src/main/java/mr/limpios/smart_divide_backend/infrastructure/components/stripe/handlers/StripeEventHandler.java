package mr.limpios.smart_divide_backend.infrastructure.components.stripe.handlers;

import com.stripe.model.StripeObject;

public interface StripeEventHandler {
  void handle(StripeObject stripeObject);
}
