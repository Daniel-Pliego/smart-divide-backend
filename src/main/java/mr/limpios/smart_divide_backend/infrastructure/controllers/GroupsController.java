package mr.limpios.smart_divide_backend.infrastructure.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mr.limpios.smart_divide_backend.application.dtos.*;
import mr.limpios.smart_divide_backend.application.services.GroupService;
import mr.limpios.smart_divide_backend.infrastructure.dtos.WrapperResponse;
import mr.limpios.smart_divide_backend.infrastructure.security.CustomUserDetails;

@RestController
@RequestMapping("groups")
@CrossOrigin(maxAge = 3600, methods = {RequestMethod.OPTIONS, RequestMethod.POST}, origins = {"*"})
@Tag(name = "Groups", description = "Endpoints for create and view groups")
public class GroupsController {

  private final GroupService groupService;

  public GroupsController(GroupService groupService) {
    this.groupService = groupService;
  }

  @Operation(summary = "Create a new group for a user")
  @PostMapping
  public ResponseEntity<WrapperResponse<GroupResumeDTO>> createGroup(
      @RequestBody CreateGroupDTO groupDataDTO) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    String userId = userDetails.getUserId();

    GroupResumeDTO groupResumeDTO = groupService.createGroup(groupDataDTO, userId);
    return new ResponseEntity<>(
        new WrapperResponse<>(true, "Group created successfully", groupResumeDTO),
        HttpStatus.CREATED);
  }

  @Operation(summary = "update information on an existing group")
  @PutMapping("{groupId}")
  public ResponseEntity<WrapperResponse<UpdateGroupResumeDTO>> updateGroup(
      @PathVariable String groupId, @RequestBody CreateGroupDTO groupDataDTO) {

    UpdateGroupResumeDTO updateGroupResumeDTO = groupService.updateGroup(groupDataDTO, groupId);
    return new ResponseEntity<>(
        new WrapperResponse<>(true, "Group updated successfully", updateGroupResumeDTO),
        HttpStatus.OK);
  }

  @Operation(summary = "Adds a member to an existing group")
  @PostMapping("{groupId}/members")
  public ResponseEntity<WrapperResponse<NewMemberDTO>> addMember(@PathVariable String groupId,
      @RequestBody AddMemberDTO addMemberDTO) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    String userId = userDetails.getUserId();

    NewMemberDTO newMemberDTO = groupService.addMemberToGroup(addMemberDTO, groupId, userId);
    return new ResponseEntity<>(
        new WrapperResponse<>(true, "Member added successfully", newMemberDTO), HttpStatus.OK);
  }

  @Operation(summary = "Get all groups of a user")
  @GetMapping
  public ResponseEntity<WrapperResponse<List<GroupResumeDTO>>> getUserGroups() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    String userId = userDetails.getUserId();

    List<GroupResumeDTO> groups = groupService.getUserGroups(userId);
    return new ResponseEntity<>(
        new WrapperResponse<>(true, "User groups retrieved successfully", groups), HttpStatus.OK);
  }

  @Operation(summary = "Get all group transactions")
  @GetMapping("{groupId}/transactions")
  public ResponseEntity<WrapperResponse<GroupTransactionHistoryDTO>> getGroupTransactions(
      @PathVariable String groupId) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    String userId = userDetails.getUserId();

    GroupTransactionHistoryDTO history = groupService.getGroupTransactionHistory(groupId, userId);

    return ResponseEntity.ok(new WrapperResponse<>(true, "Success", history));
  }

  @Operation(summary = "Get the list of members of a specific group")
  @GetMapping("{groupId}/members")
  public ResponseEntity<WrapperResponse<List<MemberResumeDTO>>> getGroupMembers(
      @PathVariable String groupId) {
    List<MemberResumeDTO> members = groupService.getGroupMembers(groupId);
    return new ResponseEntity<>(
        new WrapperResponse<>(true, "Group members retrieved successfully", members),
        HttpStatus.OK);

  }

}
