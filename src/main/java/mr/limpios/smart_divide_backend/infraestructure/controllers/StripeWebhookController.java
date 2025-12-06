package mr.limpios.smart_divide_backend.infraestructure.controllers;

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
import com.stripe.model.ExternalAccount;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;

import lombok.RequiredArgsConstructor;
import mr.limpios.smart_divide_backend.infraestructure.repositories.jpa.JPACardRepository;
import mr.limpios.smart_divide_backend.infraestructure.repositories.jpa.JPAUserRepository;
import mr.limpios.smart_divide_backend.infraestructure.repositories.jpa.StripeRepositoryJpa;
import mr.limpios.smart_divide_backend.infraestructure.schemas.CardSchema;
import mr.limpios.smart_divide_backend.infraestructure.schemas.UserSchema;

@RestController
@RequestMapping("/stripe/webhook")
@RequiredArgsConstructor
public class StripeWebhookController {

  @Value("${stripe.webhook.secret}")
  private String endpointSecret;

  private final StripeRepositoryJpa stripeRepository;
  private final JPAUserRepository userRepository;
  private final JPACardRepository cardRepository;

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

      if (account.getExternalAccounts() != null && account.getExternalAccounts().getData() != null
          && !account.getExternalAccounts().getData().isEmpty()) {

        ExternalAccount externalAccount = account.getExternalAccounts().getData().get(0);

        if (externalAccount instanceof com.stripe.model.BankAccount) {
          com.stripe.model.BankAccount bankAccount = (com.stripe.model.BankAccount) externalAccount;
          CardSchema card =
              CardSchema.builder().user(user).lastDigits(bankAccount.getLast4()).build();
          cardRepository.save(card);
        }
      }
    });
  }
}
