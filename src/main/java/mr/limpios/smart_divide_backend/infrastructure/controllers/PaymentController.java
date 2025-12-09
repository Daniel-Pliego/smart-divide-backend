package mr.limpios.smart_divide_backend.infrastructure.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mr.limpios.smart_divide_backend.application.dtos.CreatePaymentDTO;
import mr.limpios.smart_divide_backend.application.services.PaymentService;
import mr.limpios.smart_divide_backend.infrastructure.dtos.WrapperResponse;
import mr.limpios.smart_divide_backend.infrastructure.security.CustomUserDetails;

@RestController
@RequestMapping("groups/{groupId}")
@CrossOrigin(maxAge = 3600, methods = {RequestMethod.OPTIONS, RequestMethod.POST}, origins = {"*"})
@Tag(name = "Payments", description = "Endpoints to manage payments")
public class PaymentController {

  private final PaymentService paymentService;

  public PaymentController(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

  @Operation(summary = "Endpoint to create a new payment")
  @PostMapping("payments")
  public ResponseEntity<WrapperResponse<Void>> createPayment(@PathVariable String groupId,
      @RequestBody CreatePaymentDTO createPaymentDTO) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    String userId = userDetails.getUserId();
    paymentService.createPayment(userId, groupId, createPaymentDTO, false);
    return new ResponseEntity<>(new WrapperResponse<>(true, "Payment created successfully", null),
        HttpStatus.CREATED);
  }

}
