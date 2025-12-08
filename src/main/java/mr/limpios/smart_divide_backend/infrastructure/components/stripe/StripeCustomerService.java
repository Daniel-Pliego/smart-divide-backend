package mr.limpios.smart_divide_backend.infrastructure.components.stripe;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.USER_NOT_FOUND;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerSession;
import com.stripe.model.PaymentIntent;
import com.stripe.model.SetupIntent;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerSessionCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.SetupIntentCreateParams;

import jakarta.annotation.PostConstruct;
import mr.limpios.smart_divide_backend.application.repositories.ExpenseGroupBalanceRepository;
import mr.limpios.smart_divide_backend.application.repositories.UserRepository;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceNotFoundException;
import mr.limpios.smart_divide_backend.domain.models.ExpenseGroupBalance;
import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.infrastructure.components.stripe.models.CustomerSessionResponse;
import mr.limpios.smart_divide_backend.infrastructure.components.stripe.models.PaymentIntentRequest;
import mr.limpios.smart_divide_backend.infrastructure.components.stripe.models.PaymentIntentResponse;
import mr.limpios.smart_divide_backend.infrastructure.components.stripe.models.SetUpIntentResponse;
import mr.limpios.smart_divide_backend.infrastructure.repositories.impl.StripeUserInfoRepository;
import mr.limpios.smart_divide_backend.infrastructure.schemas.StripeUserSchema;

@Service
public class StripeCustomerService {

  @Value("${stripe.api.key}")
  private String stripeApiKey;

  private final StripeUserInfoRepository stripeUserInfoRepository;
  private final ExpenseGroupBalanceRepository expenseGroupBalanceRepository;
  private final UserRepository userRepository;

  public StripeCustomerService(StripeUserInfoRepository stripeUserInfoRepository,
      ExpenseGroupBalanceRepository expenseGroupBalanceRepository, UserRepository userRepository) {

    this.stripeUserInfoRepository = stripeUserInfoRepository;
    this.expenseGroupBalanceRepository = expenseGroupBalanceRepository;
    this.userRepository = userRepository;
  }

  @PostConstruct
  public void init() {
    Stripe.apiKey = stripeApiKey;
  }

  public PaymentIntentResponse handlePaymentIntent(PaymentIntentRequest payment, String groupId) {

    ExpenseGroupBalance balance = expenseGroupBalanceRepository
        .findByCreditorAndDebtorAndGroup(payment.toUser(), payment.fromUser(), groupId)
        .orElseThrow(() -> new ResourceNotFoundException("Balance not found"));

    if (payment.amount().compareTo(balance.amount()) > 0) {
      throw new IllegalArgumentException("El monto excede el adeudo pendiente.");
    }

    String customerId = stripeUserInfoRepository.getStripeCustomerId(payment.fromUser());

    String accountId = stripeUserInfoRepository.getStripeAccountId(payment.toUser());

    if (accountId == null) {
      throw new IllegalStateException("El receptor no tiene cuenta Express configurada.");
    }

    try {
      if (customerId == null) {
        customerId = createCustomerAccount(balance.debtor());
      }

      PaymentIntent intent = createPaymentIntent(payment, customerId, accountId, groupId);

      return new PaymentIntentResponse(intent.getClientSecret(), createCustomerSession(customerId),
          customerId);

    } catch (StripeException e) {
      throw new RuntimeException("Stripe error: " + e.getMessage(), e);
    }
  }

  public SetUpIntentResponse handleSetUpIntent(String userId) throws StripeException {
    User user = userRepository.getUserbyId(userId);

    if (Objects.isNull(user)) {
      throw new ResourceNotFoundException(USER_NOT_FOUND);
    }

    String customerId = stripeUserInfoRepository.getStripeCustomerId(userId);

    if (Objects.isNull(customerId)) {
      customerId = createCustomerAccount(user);
    }

    SetupIntent setupIntent = createSetupIntent(customerId);

    return new SetUpIntentResponse(setupIntent.getClientSecret());
  }

  public CustomerSessionResponse getCustomerSession(String userId) throws StripeException {
    User user = userRepository.getUserbyId(userId);

    if (Objects.isNull(user)) {
      throw new ResourceNotFoundException(USER_NOT_FOUND);
    }

    String customerId = stripeUserInfoRepository.getStripeCustomerId(userId);

    if (Objects.isNull(customerId)) {
      customerId = createCustomerAccount(user);
    }

    String customerSession = createCustomerSession(customerId);

    return new CustomerSessionResponse(customerSession, customerId);

  }

  private SetupIntent createSetupIntent(String customerId) throws StripeException {
    Customer customer = Customer.retrieve(customerId);

    SetupIntentCreateParams setupIntentCreateParams =
        SetupIntentCreateParams.builder().setCustomer(customer.getId()).build();

    SetupIntent setupIntent = SetupIntent.create(setupIntentCreateParams);

    return setupIntent;
  }

  private String createCustomerSession(String customerId) throws StripeException {

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

  private PaymentIntent createPaymentIntent(PaymentIntentRequest payment, String customerId,
      String destinationAccountId, String groupId) throws StripeException {

    Long amountInCents = payment.amount().multiply(BigDecimal.valueOf(100)).longValue();

    Map<String, String> metadata = new HashMap<>();
    metadata.put("groupId", groupId);
    metadata.put("amount", payment.amount().toString());
    metadata.put("fromUser", payment.fromUser());
    metadata.put("toUser", payment.toUser());

    PaymentIntentCreateParams params =
        PaymentIntentCreateParams.builder().setAmount(amountInCents).setCurrency("mxn")
            .setCustomer(customerId).addPaymentMethodType("card").putAllMetadata(metadata)
            .setSetupFutureUsage(PaymentIntentCreateParams.SetupFutureUsage.OFF_SESSION)
            .setTransferData(PaymentIntentCreateParams.TransferData.builder()
                .setDestination(destinationAccountId).build())
            .build();

    return PaymentIntent.create(params);
  }

  private String createCustomerAccount(User user) throws StripeException {

    StripeUserSchema stripeUser = stripeUserInfoRepository.getOrCreateStripeUser(user);

    if (stripeUser.getStripeCustomerId() != null) {
      return stripeUser.getStripeCustomerId();
    }

    CustomerCreateParams params =
        CustomerCreateParams.builder().setEmail(user.email()).setName(user.name()).build();

    Customer customer = Customer.create(params);

    stripeUser.setStripeCustomerId(customer.getId());
    stripeUserInfoRepository.save(stripeUser);

    return customer.getId();
  }
}
