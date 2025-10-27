package mr.limpios.smart_divide_backend.aplication.services;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.*;

import java.util.List;
import java.util.Objects;
import mr.limpios.smart_divide_backend.aplication.repositories.GroupRepository;
import mr.limpios.smart_divide_backend.aplication.repositories.UserRepository;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceNotFoundException;
import mr.limpios.smart_divide_backend.domain.models.Group;
import mr.limpios.smart_divide_backend.domain.models.GroupIcon;
import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.domain.validators.GroupValidator;
import mr.limpios.smart_divide_backend.infraestructure.dto.CreateGroupDTO;
import mr.limpios.smart_divide_backend.infraestructure.dto.GroupResumeDTO;
import org.springframework.stereotype.Service;

@Service
public class CreateGroupService {

  private final GroupRepository groupRepository;
  private final UserRepository userRepository;

  public CreateGroupService(GroupRepository groupRepository, UserRepository userRepository) {
    this.groupRepository = groupRepository;
    this.userRepository = userRepository;
  }

  public GroupResumeDTO createGroup(CreateGroupDTO group, String ownerId) {
    User owner = this.userRepository.getUserbyId(ownerId);

    if (Objects.isNull(owner)) {
      throw new ResourceNotFoundException(USER_NOT_FOUND);
    }

    GroupValidator.validate(group);

    Group savedGroup = this.groupRepository.saveGroup(new Group(null, group.name(),
        group.description(), new GroupIcon(group.iconId(), null), owner, List.of(owner)));

    return new GroupResumeDTO(savedGroup.id(), savedGroup.name(), savedGroup.description(),
        savedGroup.groupIcon().id(), savedGroup.owner().id(), 0, 0);
  }
}
