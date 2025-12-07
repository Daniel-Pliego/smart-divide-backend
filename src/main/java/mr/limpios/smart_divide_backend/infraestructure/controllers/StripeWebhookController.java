package mr.limpios.smart_divide_backend.infraestructure.controllers;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Account;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;

import lombok.RequiredArgsConstructor;
import mr.limpios.smart_divide_backend.aplication.services.PaymentService;
import mr.limpios.smart_divide_backend.domain.dto.CreatePaymentDTO;
import mr.limpios.smart_divide_backend.infraestructure.repositories.jpa.JPAStripeRepository;
import mr.limpios.smart_divide_backend.infraestructure.repositories.jpa.JPAUserRepository;
import mr.limpios.smart_divide_backend.infraestructure.schemas.UserSchema;

@RestController
@RequestMapping("/stripe/webhook")
@RequiredArgsConstructor
public class StripeWebhookController {

  @Value("${stripe.webhook.secret}")
  private String endpointSecret;

  private final JPAStripeRepository stripeRepository;
  private final JPAUserRepository userRepository;
  private final PaymentService paymentService;

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

    if ("account.updated".equals(event.getType())) {
      EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
      StripeObject stripeObject = null;
      if (dataObjectDeserializer.getObject().isPresent()) {
        stripeObject = dataObjectDeserializer.getObject().get();
      } else {
        try {
          stripeObject = dataObjectDeserializer.deserializeUnsafe();

        } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook error");
        }
      }

      Account account = (Account) stripeObject;
      handleAccountUpdated(account);
    }

    if ("payment_intent.succeeded".equals(event.getType())) {
      EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
      StripeObject stripeObject = null;
      if (dataObjectDeserializer.getObject().isPresent()) {
        stripeObject = dataObjectDeserializer.getObject().get();
      } else {
        try {
          stripeObject = dataObjectDeserializer.deserializeUnsafe();

        } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook error");
        }
      }

      PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
      Map<String, String> metadata = paymentIntent.getMetadata();
      String groupId = metadata.get("groupId");
      String fromUser = metadata.get("fromUser");
      String toUser = metadata.get("toUser");
      String amount = metadata.get("amount");

      paymentService.createPayment(fromUser, groupId,
          new CreatePaymentDTO(fromUser, toUser, new BigDecimal(amount)), true);
    }

    return ResponseEntity.ok("Received");
  }

  private void handleAccountUpdated(Account account) {
    stripeRepository.findByStripeAccountId(account.getId()).ifPresent(stripeUser -> {
      UserSchema user = stripeUser.getUser();

      if (account.getDetailsSubmitted() || account.getChargesEnabled()) {
        if (!user.getIsVerified()) {
          user.setIsVerified(true);
          userRepository.save(user);
        }
      }

    });
  }
}
