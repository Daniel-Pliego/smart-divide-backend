package mr.limpios.smart_divide_backend.infraestructure.controllers;

import mr.limpios.smart_divide_backend.infraestructure.dto.UpdateGroupResumeDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mr.limpios.smart_divide_backend.aplication.services.GroupService;
import mr.limpios.smart_divide_backend.infraestructure.dto.GroupDataDTO;
import mr.limpios.smart_divide_backend.infraestructure.dto.GroupResumeDTO;
import mr.limpios.smart_divide_backend.infraestructure.dto.WrapperResponse;

@RestController
@RequestMapping("user")
@CrossOrigin(maxAge = 3600, methods = {RequestMethod.OPTIONS, RequestMethod.POST}, origins = {"*"})
@Tag(name = "Groups", description = "Endpoints for create and view groups")
public class GroupsController {

  private final GroupService groupService;

  public GroupsController(GroupService groupService) {
    this.groupService = groupService;
  }

  @Operation(summary = "Create a new group for a user")
  @PostMapping("{userId}/groups")
  public ResponseEntity<WrapperResponse<GroupResumeDTO>> createGroup(
          @PathVariable String userId,
          @RequestBody GroupDataDTO groupDataDTO) {
    GroupResumeDTO groupResumeDTO = groupService.createGroup(groupDataDTO, userId);
    return new ResponseEntity<>(
        new WrapperResponse<>(true, "Group created successfully", groupResumeDTO),
        HttpStatus.CREATED);
  }

  @Operation(summary = "update information on an existing group")
  @PutMapping("{userId}/groups/{groupId}")
  public ResponseEntity<WrapperResponse<UpdateGroupResumeDTO>> updateGroup(
          @PathVariable String userId,
          @PathVariable String groupId,
          @RequestBody GroupDataDTO groupDataDTO) {

      UpdateGroupResumeDTO updateGroupResumeDTO =  groupService.updateGroup(groupDataDTO, groupId);
      return new ResponseEntity<>(
              new WrapperResponse<>(true, "Group updated successfully", updateGroupResumeDTO),
              HttpStatus.OK);
  }
}
