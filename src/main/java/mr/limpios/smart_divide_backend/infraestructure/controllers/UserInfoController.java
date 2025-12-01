package mr.limpios.smart_divide_backend.infraestructure.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mr.limpios.smart_divide_backend.aplication.repositories.UserRepository;
import mr.limpios.smart_divide_backend.aplication.services.UserInfoService;
import mr.limpios.smart_divide_backend.domain.dto.UserDetailsDTO;
import mr.limpios.smart_divide_backend.domain.dto.WrapperResponse;
import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.infraestructure.security.JWTService;


@RestController
@RequestMapping("user")
@CrossOrigin(maxAge = 3600, methods = {RequestMethod.OPTIONS, RequestMethod.GET}, origins = {"*"})
@Tag(name = "User Info", description = "Endpoints for update, view and delete user account")
public class UserInfoController {

  private final UserInfoService userInfoService;
  private final JWTService jwtService;
  private final UserRepository userRepository;

  public UserInfoController(UserInfoService userInfoService, JWTService jwtService,
      UserRepository userRepository) {
    this.userInfoService = userInfoService;
    this.jwtService = jwtService;
    this.userRepository = userRepository;
  }

  @Operation(summary = "Get user information by user ID")
  @GetMapping("/{userId}")
  public ResponseEntity<WrapperResponse<UserDetailsDTO>> getUserInfo(@RequestParam String userId) {
    UserDetailsDTO user = this.userInfoService.getUserInfo(userId);

    return new ResponseEntity<>(
        new WrapperResponse<>(true, "User info retrieved successfully", user), HttpStatus.OK);
  }

  @Operation(summary = "Save push notification token")
  @PostMapping("/push-token")
  public ResponseEntity<WrapperResponse<Void>> savePushToken(
      @RequestHeader("Authorization") String token, @RequestBody Map<String, String> request) {

    String userId = jwtService.getUserIdFromToken(token);
    String pushToken = request.get("pushToken");

    User user = userRepository.getUserbyId(userId);
    User updatedUser = new User(user.id(), user.name(), user.lastName(), user.email(),
        user.password(), user.photoUrl(), user.isVerified(), user.cards(), pushToken);

    userRepository.saveUser(updatedUser);

    return ResponseEntity.ok(new WrapperResponse<>(true, "Push token guardado", null));
  }



}
