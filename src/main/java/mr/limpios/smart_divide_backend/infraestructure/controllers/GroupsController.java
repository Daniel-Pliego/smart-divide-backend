package mr.limpios.smart_divide_backend.infraestructure.controllers;

import mr.limpios.smart_divide_backend.aplication.services.CreateGroupService;
import mr.limpios.smart_divide_backend.infraestructure.dto.CreateGroupDTO;
import mr.limpios.smart_divide_backend.infraestructure.dto.GroupResumeDTO;
import mr.limpios.smart_divide_backend.infraestructure.dto.WrapperResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("user")
@CrossOrigin(maxAge = 3600, methods = {RequestMethod.OPTIONS, RequestMethod.POST}, origins = {"*"})
@Tag(name = "Groups", description = "Endpoints for create and view groups")
public class GroupsController {

        private final CreateGroupService createGroupService;

        public GroupsController(CreateGroupService createGroupService) {
                this.createGroupService = createGroupService;
        }

        @Operation(summary = "Create a new group for a user")
        @PostMapping("{userId}/groups")
        public ResponseEntity<WrapperResponse<GroupResumeDTO>> createGroup(
                        @PathVariable String userId, @RequestBody CreateGroupDTO createGroupDTO) {
                GroupResumeDTO groupResumeDTO =
                                createGroupService.createGroup(createGroupDTO, userId);
                return new ResponseEntity<>(new WrapperResponse<>(true,
                                "Group created successfully", groupResumeDTO), HttpStatus.CREATED);
        }
}
