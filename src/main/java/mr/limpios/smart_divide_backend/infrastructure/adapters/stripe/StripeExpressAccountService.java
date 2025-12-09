package mr.limpios.smart_divide_backend.infrastructure.adapters.stripe;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.USER_NOT_FOUND;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.model.CustomerSession;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import com.stripe.param.CustomerSessionCreateParams;

import jakarta.annotation.PostConstruct;
import mr.limpios.smart_divide_backend.application.repositories.UserRepository;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceNotFoundException;
import mr.limpios.smart_divide_backend.infrastructure.repositories.impl.StripeUserInfoRepository;
import mr.limpios.smart_divide_backend.infrastructure.schemas.StripeUserSchema;

@Service
public class StripeExpressAccountService {

  @Value("${stripe.api.key}")
  private String stripeApiKey;

  private final StripeUserInfoRepository stripeUserInfoRepository;
  private final UserRepository userRepository;

  public StripeExpressAccountService(StripeUserInfoRepository stripeUserInfoRepository,
      UserRepository userRepository) {
    this.stripeUserInfoRepository = stripeUserInfoRepository;
    this.userRepository = userRepository;
  }

  @PostConstruct
  public void init() {
    Stripe.apiKey = stripeApiKey;
  }

  private String createExpressAccount(StripeUserSchema user) throws StripeException {

    AccountCreateParams params =
        AccountCreateParams.builder().setType(AccountCreateParams.Type.EXPRESS).setCountry("MX")
            .setEmail(user.getUser().getEmail())
            .setBusinessType(AccountCreateParams.BusinessType.INDIVIDUAL)
            .setCapabilities(AccountCreateParams.Capabilities.builder()
                .setTransfers(
                    AccountCreateParams.Capabilities.Transfers.builder().setRequested(true).build())
                .build())
            .setBusinessProfile(AccountCreateParams.BusinessProfile.builder().setMcc("7399")
                .setProductDescription("Recibir pagos compartidos").build())
            .build();

    Account account = Account.create(params);
    user.setStripeAccountId(account.getId());

    stripeUserInfoRepository.save(user);

    return account.getId();
  }

  public String getAccountLink(String userId) throws StripeException {

    var user = userRepository.getUserbyId(userId);
    if (Objects.isNull(user)) {
      throw new ResourceNotFoundException(USER_NOT_FOUND);
    }

    var stripeUser = stripeUserInfoRepository.getOrCreateStripeUser(user);

    String stripeAccountId = stripeUser.getStripeAccountId();

    if (stripeAccountId == null || stripeAccountId.isBlank()) {
      stripeAccountId = createExpressAccount(stripeUser);
    }

    AccountLinkCreateParams params = AccountLinkCreateParams.builder().setAccount(stripeAccountId)
        .setRefreshUrl("https://example.com/refresh").setReturnUrl("https://example.com/return")
        .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING).build();
    AccountLink accountLink = AccountLink.create(params);
    return accountLink.getUrl();
  }

  public String createCustomerSession(String customerId) throws StripeException {

    CustomerSessionCreateParams params =
        CustomerSessionCreateParams.builder().setCustomer(customerId)
            .setComponents(CustomerSessionCreateParams.Components.builder().build())
            .putExtraParam("components[mobile_payment_element][enabled]", true)
            .putExtraParam("components[mobile_payment_element][features][payment_method_save]",
                "enabled")
            .putExtraParam("components[mobile_payment_element][features][payment_method_redisplay]",
                "enabled")
            .putExtraParam("components[mobile_payment_element][features][payment_method_remove]",
                "enabled")
            .build();

    CustomerSession session = CustomerSession.create(params);
    return session.getClientSecret();
  }

}
