package mr.limpios.smart_divide_backend.infrastructure.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mr.limpios.smart_divide_backend.application.dtos.Auth.AuthenticatedDTO;
import mr.limpios.smart_divide_backend.application.dtos.Auth.UserSignInDTO;
import mr.limpios.smart_divide_backend.application.dtos.Auth.UserSignUpDTO;
import mr.limpios.smart_divide_backend.application.services.AuthService;
import mr.limpios.smart_divide_backend.infrastructure.dtos.WrapperResponse;

@RestController
@RequestMapping("auth")
@CrossOrigin(maxAge = 3600, methods = {RequestMethod.OPTIONS, RequestMethod.POST}, origins = {"*"})
@Tag(name = "Authentication", description = "Endpoints for user authentication")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @Operation(summary = "Registers a new user in the system")
  @PostMapping("sign-up")
  public ResponseEntity<WrapperResponse<AuthenticatedDTO>> signUp(
      @RequestBody UserSignUpDTO userSignUpDTO) {
    AuthenticatedDTO entity = authService.signUp(userSignUpDTO);
    WrapperResponse<AuthenticatedDTO> response =
        new WrapperResponse<>(true, "User created", entity);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @Operation(summary = "Authenticates a user and returns a JWT token")
  @PostMapping("sign-in")
  public ResponseEntity<WrapperResponse<AuthenticatedDTO>> signIn(
      @RequestBody UserSignInDTO loginRequestDTO) {

    AuthenticatedDTO entity = authService.signIn(loginRequestDTO);
    WrapperResponse<AuthenticatedDTO> response =
        new WrapperResponse<>(true, "User authenticated", entity);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
