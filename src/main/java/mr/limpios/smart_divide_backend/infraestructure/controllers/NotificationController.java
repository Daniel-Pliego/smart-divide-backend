package mr.limpios.smart_divide_backend.infraestructure.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import mr.limpios.smart_divide_backend.aplication.dtos.RegisterDeviceTokenDTO;
import mr.limpios.smart_divide_backend.aplication.services.NotificationService;
import mr.limpios.smart_divide_backend.infraestructure.security.CustomUserDetails;

@RestController
@RequestMapping("/notifications")
@CrossOrigin(maxAge = 3600, methods = {RequestMethod.OPTIONS, RequestMethod.POST}, origins = {"*"})
@Tag(name = "Notifications", description = "Endpoints to register device tokens")
public class NotificationController {

  private final NotificationService notificationService;

  public NotificationController(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @PostMapping("/token")
  public ResponseEntity<Void> registerToken(@RequestBody RegisterDeviceTokenDTO dto) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    String userId = userDetails.getUserId();

    notificationService.registerToken(userId, dto);
    return ResponseEntity.ok().build();
  }
}
