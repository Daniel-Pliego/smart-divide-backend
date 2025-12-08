package mr.limpios.smart_divide_backend.infraestructure.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mr.limpios.smart_divide_backend.aplication.services.UserInfoService;
import mr.limpios.smart_divide_backend.domain.dto.UserDetailsDTO;
import mr.limpios.smart_divide_backend.domain.dto.WrapperResponse;


@RestController
@RequestMapping("user")
@CrossOrigin(maxAge = 3600, methods = {RequestMethod.OPTIONS, RequestMethod.GET}, origins = {"*"})
@Tag(name = "User Info", description = "Endpoints for update, view and delete user account")
public class UserInfoController {

  private final UserInfoService userInfoService;

  public UserInfoController(UserInfoService userInfoService) {
    this.userInfoService = userInfoService;
  }

  @Operation(summary = "Get user information by user ID")
  @GetMapping("/{userId}")
  public ResponseEntity<WrapperResponse<UserDetailsDTO>> getUserInfo(@PathVariable String userId) {
    UserDetailsDTO user = this.userInfoService.getUserInfo(userId);

    return new ResponseEntity<>(
        new WrapperResponse<>(true, "User info retrieved successfully", user), HttpStatus.OK);
  }
}
