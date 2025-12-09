package mr.limpios.smart_divide_backend.infrastructure.components.stripe.handlers;

import org.springframework.stereotype.Component;

import com.stripe.model.Account;

import lombok.AllArgsConstructor;
import mr.limpios.smart_divide_backend.infrastructure.repositories.jpa.JPAStripeRepository;
import mr.limpios.smart_divide_backend.infrastructure.repositories.jpa.JPAUserRepository;
import mr.limpios.smart_divide_backend.infrastructure.schemas.UserSchema;

@Component
@AllArgsConstructor
public class AccountUpdatedHandler {

  private final JPAStripeRepository stripeRepository;
  private final JPAUserRepository userRepository;

  public void handle(Account account) {
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
