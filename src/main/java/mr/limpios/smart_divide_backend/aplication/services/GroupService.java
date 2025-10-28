package mr.limpios.smart_divide_backend.aplication.services;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.USER_NOT_FOUND;

import java.util.List;
import java.util.Objects;

import mr.limpios.smart_divide_backend.infraestructure.dto.UpdateGroupResumeDTO;
import org.springframework.stereotype.Service;

import mr.limpios.smart_divide_backend.aplication.repositories.GroupRepository;
import mr.limpios.smart_divide_backend.aplication.repositories.UserRepository;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceNotFoundException;
import mr.limpios.smart_divide_backend.domain.models.Group;
import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.domain.validators.GroupValidator;
import mr.limpios.smart_divide_backend.infraestructure.dto.GroupDataDTO;
import mr.limpios.smart_divide_backend.infraestructure.dto.GroupResumeDTO;

@Service
public class GroupService {

  private final GroupRepository groupRepository;
  private final UserRepository userRepository;

  public GroupService(GroupRepository groupRepository, UserRepository userRepository) {
    this.groupRepository = groupRepository;
    this.userRepository = userRepository;
  }

  public GroupResumeDTO createGroup(GroupDataDTO group, String ownerId) {
    User owner = this.userRepository.getUserbyId(ownerId);

    if (Objects.isNull(owner)) {
      throw new ResourceNotFoundException(USER_NOT_FOUND);
    }

    GroupValidator.validate(group);

    Group savedGroup = this.groupRepository.saveGroup(
            new Group(
                null,
                group.name(),
                group.description(),
                owner,
                List.of(owner)
            ));

    return new GroupResumeDTO(
            savedGroup.id(),
            savedGroup.name(),
            savedGroup.description(),
            savedGroup.owner().id(),
            0,
            0);
  }

    public UpdateGroupResumeDTO updateGroup(GroupDataDTO group, String groupId) {
        GroupValidator.validate(group);

        Group findedGroup = this.groupRepository.getGroupById(groupId);

        Group updatedGroup = this.groupRepository.updateGroupById(
                groupId,
                new Group(
                        findedGroup.id(),
                        group.name(),
                        group.description(),
                        findedGroup.owner(),
                        findedGroup.members()
                ));

        return new UpdateGroupResumeDTO(
                updatedGroup.id(),
                updatedGroup.name(),
                updatedGroup.description());
    }
}
