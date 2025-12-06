package mr.limpios.smart_divide_backend.infraestructure.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentMethodAttachParams;

import jakarta.annotation.PostConstruct;
import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;
import mr.limpios.smart_divide_backend.infraestructure.repositories.jpa.JPACardRepository;
import mr.limpios.smart_divide_backend.infraestructure.repositories.jpa.StripeRepositoryJpa;
import mr.limpios.smart_divide_backend.infraestructure.schemas.CardSchema;
import mr.limpios.smart_divide_backend.infraestructure.schemas.StripeUserSchema;
import mr.limpios.smart_divide_backend.infraestructure.schemas.UserSchema;

@Service
public class StripeService {

  @Value("${stripe.api.key}")
  private String stripeApiKey;

  private final StripeRepositoryJpa stripeRepository;
  private final JPACardRepository cardRepository;

  public StripeService(StripeRepositoryJpa stripeRepository, JPACardRepository cardRepository) {
    this.stripeRepository = stripeRepository;
    this.cardRepository = cardRepository;
  }

  @PostConstruct
  public void init() {
    Stripe.apiKey = stripeApiKey;
  }

  public StripeUserSchema getOrCreateStripeUser(UserSchema user) {
    return stripeRepository.findByUserId(user.getId())
        .orElseGet(() -> stripeRepository.save(StripeUserSchema.builder().user(user).build()));
  }

  /**
   * Creates an Express account for a regular user (individual).
   */
  public String createExpressAccount(UserSchema user) {
    try {
      StripeUserSchema stripeUser = getOrCreateStripeUser(user);

      if (stripeUser.getStripeAccountId() != null) {
        return stripeUser.getStripeAccountId();
      }

      AccountCreateParams params =
          AccountCreateParams.builder().setType(AccountCreateParams.Type.EXPRESS).setCountry("MX")
              .setEmail(user.getEmail())
              .setCapabilities(AccountCreateParams.Capabilities.builder().setTransfers(
                  AccountCreateParams.Capabilities.Transfers.builder().setRequested(true).build())
                  .build())
              .build();

      Account account = Account.create(params);

      stripeUser.setStripeAccountId(account.getId());
      stripeRepository.save(stripeUser);

      return account.getId();
    } catch (StripeException e) {
      throw new InvalidDataException("Error creating Stripe account: " + e.getMessage());
    }
  }

  public String createAccountLink(String accountId, String returnUrl, String refreshUrl) {
    try {
      AccountLinkCreateParams params = AccountLinkCreateParams.builder().setAccount(accountId)
          .setReturnUrl(returnUrl).setRefreshUrl(refreshUrl)
          .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING).build();

      return AccountLink.create(params).getUrl();
    } catch (StripeException e) {
      throw new InvalidDataException("Error creating account link: " + e.getMessage());
    }
  }

  public void registerCard(UserSchema user, String paymentMethodId) {
    try {
      StripeUserSchema stripeUser = getOrCreateStripeUser(user);

      if (stripeUser.getStripeCustomerId() == null) {
        Customer customer = Customer.create(CustomerCreateParams.builder().setEmail(user.getEmail())
            .setName(user.getName() + " " + user.getLastName()).build());

        stripeUser.setStripeCustomerId(customer.getId());
        stripeRepository.save(stripeUser);
      }

      PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
      paymentMethod.attach(PaymentMethodAttachParams.builder()
          .setCustomer(stripeUser.getStripeCustomerId()).build());

      CardSchema card = CardSchema.builder().user(user).brand(paymentMethod.getCard().getBrand())
          .lastDigits(paymentMethod.getCard().getLast4())
          .expMonth(String.valueOf(paymentMethod.getCard().getExpMonth()))
          .expYear(String.valueOf(paymentMethod.getCard().getExpYear())).build();

      cardRepository.save(card);

    } catch (StripeException e) {
      throw new InvalidDataException("Error registering card: " + e.getMessage());
    }
  }
}
