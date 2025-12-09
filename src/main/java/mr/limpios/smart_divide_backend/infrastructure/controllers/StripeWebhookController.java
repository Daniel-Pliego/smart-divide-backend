package mr.limpios.smart_divide_backend.infrastructure.controllers;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;

import mr.limpios.smart_divide_backend.infrastructure.adapters.stripe.handlers.StripeEventHandler;
import mr.limpios.smart_divide_backend.infrastructure.adapters.stripe.handlers.StripeEventHandlerFactory;

@RestController
@RequestMapping("/stripe/webhook")
public class StripeWebhookController {

  @Value("${stripe.webhook.secret}")
  private String endpointSecret;

  private final StripeEventHandlerFactory eventHandlerFactory;

  public StripeWebhookController(StripeEventHandlerFactory eventHandlerFactory) {
    this.eventHandlerFactory = eventHandlerFactory;
  }

  @PostMapping
  public ResponseEntity<String> handleWebhook(@RequestBody String payload,
      @RequestHeader("Stripe-Signature") String sigHeader) {

    Event event;

    try {
      event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
    } catch (SignatureVerificationException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook error");
    }

    Map<String, StripeEventHandler> handlers = eventHandlerFactory.getHandlers();
    StripeEventHandler handler = handlers.get(event.getType());
    if (handler != null) {
      deserializeStripeObject(event.getDataObjectDeserializer()).ifPresent(handler::handle);
    }

    return ResponseEntity.ok("Received");
  }

  private Optional<StripeObject> deserializeStripeObject(
      EventDataObjectDeserializer dataObjectDeserializer) {
    if (dataObjectDeserializer.getObject().isPresent()) {
      return dataObjectDeserializer.getObject();
    }
    try {
      return Optional.of(dataObjectDeserializer.deserializeUnsafe());
    } catch (Exception e) {
      return Optional.empty();
    }
  }
}
