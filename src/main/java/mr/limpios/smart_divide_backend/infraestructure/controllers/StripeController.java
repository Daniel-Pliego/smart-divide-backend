package mr.limpios.smart_divide_backend.infraestructure.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import mr.limpios.smart_divide_backend.domain.dto.WrapperResponse;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceNotFoundException;
import mr.limpios.smart_divide_backend.infraestructure.repositories.jpa.JPAUserRepository;
import mr.limpios.smart_divide_backend.infraestructure.schemas.UserSchema;
import mr.limpios.smart_divide_backend.infraestructure.services.StripeService;

@RestController
@RequestMapping("/api/stripe")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class StripeController {

  private final StripeService stripeService;
  private final JPAUserRepository userRepository;

  @PostMapping("/onboarding/link")
  public ResponseEntity<WrapperResponse<Map<String, String>>> generateOnboardingLink(
      @RequestParam String userId, @RequestParam String returnUrl,
      @RequestParam String refreshUrl) {

    UserSchema user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    String accountId = stripeService.createExpressAccount(user);
    String url = stripeService.createAccountLink(accountId, returnUrl, refreshUrl);

    Map<String, String> response = new HashMap<>();
    response.put("url", url);

    return new ResponseEntity<>(new WrapperResponse<>(true, "Onboarding link generated", response),
        HttpStatus.OK);
  }

  @PostMapping("/card")
  public ResponseEntity<WrapperResponse<Void>> registerCard(@RequestParam String userId,
      @RequestParam String token) {

    UserSchema user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    stripeService.registerCard(user, token);

    return new ResponseEntity<>(new WrapperResponse<>(true, "Card registered successfully", null),
        HttpStatus.CREATED);
  }
}
