package mr.limpios.smart_divide_backend.infrastructure.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.exception.StripeException;

import mr.limpios.smart_divide_backend.infrastructure.components.stripe.StripeCustomerService;
import mr.limpios.smart_divide_backend.infrastructure.components.stripe.StripeExpressAccountService;
import mr.limpios.smart_divide_backend.infrastructure.dtos.WrapperResponse;
import mr.limpios.smart_divide_backend.infrastructure.dtos.stripe.CustomerSessionResponse;
import mr.limpios.smart_divide_backend.infrastructure.dtos.stripe.PaymentIntentRequest;
import mr.limpios.smart_divide_backend.infrastructure.dtos.stripe.PaymentIntentResponse;
import mr.limpios.smart_divide_backend.infrastructure.dtos.stripe.SetUpIntentResponse;
import mr.limpios.smart_divide_backend.infrastructure.security.CustomUserDetails;

@RestController
@RequestMapping("/stripe")
@CrossOrigin(origins = "*")
public class StripeController {

  private final StripeCustomerService stripeCustomerService;
  private final StripeExpressAccountService stripeExpressAccountService;

  public StripeController(StripeCustomerService stripeCustomerService,
      StripeExpressAccountService stripeExpressAccountService) {
    this.stripeCustomerService = stripeCustomerService;
    this.stripeExpressAccountService = stripeExpressAccountService;

  }

  @PostMapping("/onboarding/link")
  public ResponseEntity<WrapperResponse<Map<String, String>>> generateOnboardingLink()
      throws StripeException {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    String userId = userDetails.getUserId();

    String oboardingLink = stripeExpressAccountService.getAccountLink(userId);

    Map<String, String> response = new HashMap<>();
    response.put("url", oboardingLink);

    return new ResponseEntity<>(new WrapperResponse<>(true, "Onboarding link generated", response),
        HttpStatus.OK);
  }

  @GetMapping("/customerSession")
  public ResponseEntity<WrapperResponse<CustomerSessionResponse>> createCustomerSession()
      throws StripeException {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    String userId = userDetails.getUserId();

    CustomerSessionResponse entity = stripeCustomerService.getCustomerSession(userId);

    return new ResponseEntity<>(new WrapperResponse<>(true, "customer session created", entity),
        HttpStatus.OK);

  }

  @GetMapping("/setupIntent")
  public ResponseEntity<WrapperResponse<SetUpIntentResponse>> createSetupIntent()
      throws StripeException {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    String userId = userDetails.getUserId();

    SetUpIntentResponse entity = stripeCustomerService.handleSetUpIntent(userId);

    return new ResponseEntity<>(new WrapperResponse<>(true, "setup intent created", entity),
        HttpStatus.OK);
  }

  @PostMapping("/{groupId}/paymentIntent")
  public ResponseEntity<WrapperResponse<PaymentIntentResponse>> createPaymentIntent(
      @RequestBody PaymentIntentRequest request, @PathVariable String groupId)
      throws StripeException {

    PaymentIntentResponse response = stripeCustomerService.handlePaymentIntent(request, groupId);

    return new ResponseEntity<>(new WrapperResponse<>(true, "payment intent created", response),
        HttpStatus.OK);
  }

}
