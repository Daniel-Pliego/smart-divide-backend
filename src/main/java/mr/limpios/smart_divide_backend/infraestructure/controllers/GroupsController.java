package mr.limpios.smart_divide_backend.infraestructure.controllers;

import mr.limpios.smart_divide_backend.aplication.services.CreateGroupService;
import mr.limpios.smart_divide_backend.domain.dto.CreateGroupDTO;
import mr.limpios.smart_divide_backend.domain.dto.GroupDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class GroupsController {

    private final CreateGroupService createGroupService;

    public GroupsController(CreateGroupService createGroupService) {
        this.createGroupService = createGroupService;
    }

    @PostMapping("user/{userId}/groups")
    public ResponseEntity<GroupDTO> createGroup(
            @PathVariable String userId,
            @RequestBody CreateGroupDTO createGroupDTO
    ) {
        GroupDTO groupDTO = createGroupService.createGroup(createGroupDTO, userId);
        return new ResponseEntity<>(groupDTO, HttpStatus.CREATED);
    }
}
