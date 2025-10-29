package mr.limpios.smart_divide_backend.aplication.services;

import java.util.List;
import java.util.Objects;

import mr.limpios.smart_divide_backend.aplication.repositories.FriendshipRepository;
import mr.limpios.smart_divide_backend.domain.exceptions.FriendshipNotFoundException;
import mr.limpios.smart_divide_backend.infraestructure.dto.*;
import org.springframework.stereotype.Service;

import mr.limpios.smart_divide_backend.aplication.repositories.GroupRepository;
import mr.limpios.smart_divide_backend.aplication.repositories.UserRepository;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceNotFoundException;
import mr.limpios.smart_divide_backend.domain.models.Group;
import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.domain.validators.GroupValidator;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.*;

@Service
public class GroupService {

  private final GroupRepository groupRepository;
  private final UserRepository userRepository;
  private final FriendshipRepository friendshipRepository;

  public GroupService(GroupRepository groupRepository, UserRepository userRepository, FriendshipRepository friendshipRepository) {
    this.groupRepository = groupRepository;
    this.userRepository = userRepository;
    this.friendshipRepository = friendshipRepository;
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

        if (Objects.isNull(findedGroup)) {
            throw new ResourceNotFoundException(GROUP_NOT_FOUND);
        }

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

    public UpdatedGroupMembers addMemberToGroup(AddMemberDTO addMemberDTO, String groupId, String ownerId) {
        User owner = this.userRepository.getUserbyId(ownerId);
        User memberToAdd = this.userRepository.getUserbyId(addMemberDTO.memberId());

        if (Objects.isNull(owner) || Objects.isNull(memberToAdd)) {
            throw new ResourceNotFoundException(USER_NOT_FOUND);
        }

        Group group = groupRepository.getGroupById(groupId);

        if (Objects.isNull(group)) {
            throw new ResourceNotFoundException(GROUP_NOT_FOUND);
        }

        Boolean isFriend = this.friendshipRepository.areFriends(ownerId, memberToAdd.id());

        if (!isFriend) {
            throw new FriendshipNotFoundException(FRIENDSHIP_NOT_FOUND);
        }

        Group updatedGroup = this.groupRepository.addMemberToGroup(groupId, memberToAdd.id());

        return new UpdatedGroupMembers(
                updatedGroup.id(),
                memberToAdd.id(),
                memberToAdd.name(),
                memberToAdd.lastName(),
                memberToAdd.photoUrl()
        );

    }
}
