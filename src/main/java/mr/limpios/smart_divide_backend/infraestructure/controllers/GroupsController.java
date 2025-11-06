package mr.limpios.smart_divide_backend.infraestructure.controllers;

import mr.limpios.smart_divide_backend.infraestructure.dto.*;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mr.limpios.smart_divide_backend.aplication.services.GroupService;

@RestController
@RequestMapping("user/{userId}")
@CrossOrigin(maxAge = 3600, methods = { RequestMethod.OPTIONS, RequestMethod.POST }, origins = { "*" })
@Tag(name = "Groups", description = "Endpoints for create and view groups")
public class GroupsController {

        private final GroupService groupService;

        public GroupsController(GroupService groupService) {
                this.groupService = groupService;
        }

        @Operation(summary = "Create a new group for a user")
        @PostMapping("groups")
        public ResponseEntity<WrapperResponse<GroupResumeDTO>> createGroup(
                        @PathVariable String userId,
                        @RequestBody GroupDataDTO groupDataDTO) {
                GroupResumeDTO groupResumeDTO = groupService.createGroup(groupDataDTO, userId);
                return new ResponseEntity<>(
                                new WrapperResponse<>(true, "Group created successfully", groupResumeDTO),
                                HttpStatus.CREATED);
        }

        @Operation(summary = "update information on an existing group")
        @PutMapping("groups/{groupId}")
        public ResponseEntity<WrapperResponse<UpdateGroupResumeDTO>> updateGroup(
                        @PathVariable String userId,
                        @PathVariable String groupId,
                        @RequestBody GroupDataDTO groupDataDTO) {

                UpdateGroupResumeDTO updateGroupResumeDTO = groupService.updateGroup(groupDataDTO, groupId);
                return new ResponseEntity<>(
                                new WrapperResponse<>(true, "Group updated successfully", updateGroupResumeDTO),
                                HttpStatus.OK);
        }

        @Operation(summary = "Adds a member to an existing group")
        @PutMapping("groups/{groupId}/members")
        public ResponseEntity<WrapperResponse<NewMemberDTO>> addMember(
                        @PathVariable String userId,
                        @PathVariable String groupId,
                        @RequestBody AddMemberDTO addMemberDTO) {

                NewMemberDTO newMemberDTO = groupService.addMemberToGroup(addMemberDTO, groupId, userId);
                return new ResponseEntity<>(
                                new WrapperResponse<>(true, "Member added successfully", newMemberDTO),
                                HttpStatus.OK);
        }

        @Operation(summary = "Get all groups of a user")
        @GetMapping("groups")
        public ResponseEntity<WrapperResponse<List<GroupDataDTO>>> getUserGroups(
                        @PathVariable String userId) {
                                List<GroupDataDTO> groups = groupService.getUserGroups(userId);
                return new ResponseEntity<>(
                                new WrapperResponse<>(true, "User groups retrieved successfully", groups),
                                HttpStatus.OK);
        }

}
