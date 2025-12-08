package mr.limpios.smart_divide_backend.infraestructure.controllers;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mr.limpios.smart_divide_backend.aplication.services.FriendshipService;
import mr.limpios.smart_divide_backend.domain.dto.CreateFriendshipDTO;
import mr.limpios.smart_divide_backend.domain.dto.FriendshipDTO;
import mr.limpios.smart_divide_backend.domain.dto.WrapperResponse;
import mr.limpios.smart_divide_backend.infraestructure.security.CustomUserDetails;

@RestController
@RequestMapping("friendship")
@CrossOrigin(maxAge = 3600,
    methods = {RequestMethod.OPTIONS, RequestMethod.POST, RequestMethod.GET}, origins = {"*"})
@Tag(name = "Friendship", description = "Endpoints for add and get friendships between users")
public class FriendshipController {

  private final FriendshipService friendshipService;

  public FriendshipController(FriendshipService friendshipService) {
    this.friendshipService = friendshipService;
  }

  @Operation(summary = "Create a relationship between two users")
  @PostMapping
  public ResponseEntity<WrapperResponse<Object>> createFriendRelationship(
      @RequestBody CreateFriendshipDTO friendshipDTO) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    String userId = userDetails.getUserId();

    friendshipService.createFriendRequest(userId, friendshipDTO.friendId());
    return new ResponseEntity<>(
        new WrapperResponse<>(true, "Friend request created successfully", null),
        HttpStatus.CREATED);
  }

  @Operation(summary = "Get all friendships of a user")
  @GetMapping
  public ResponseEntity<WrapperResponse<Set<FriendshipDTO>>> getAllFriendsFromUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    String userId = userDetails.getUserId();

    Set<FriendshipDTO> friendships = friendshipService.getAllFriendsFromUser(userId);

    return new ResponseEntity<>(
        new WrapperResponse<>(true, "Friendships retrieved successfully", friendships),
        HttpStatus.OK);
  }

}
