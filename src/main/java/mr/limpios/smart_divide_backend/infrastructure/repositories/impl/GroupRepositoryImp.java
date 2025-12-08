package mr.limpios.smart_divide_backend.infrastructure.repositories.impl;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.EXISTING_FRIEND_IN_THE_GROUP;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import mr.limpios.smart_divide_backend.application.repositories.GroupRepository;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceExistException;
import mr.limpios.smart_divide_backend.domain.models.Group;
import mr.limpios.smart_divide_backend.infrastructure.mappers.GroupMapper;
import mr.limpios.smart_divide_backend.infrastructure.repositories.jpa.JPAGroupRepository;
import mr.limpios.smart_divide_backend.infrastructure.repositories.jpa.JPAUserRepository;
import mr.limpios.smart_divide_backend.infrastructure.schemas.GroupSchema;
import mr.limpios.smart_divide_backend.infrastructure.schemas.UserSchema;

@Repository
public class GroupRepositoryImp implements GroupRepository {

  private JPAGroupRepository jpaGroupRepository;
  private JPAUserRepository jpaUserRepository;

  public GroupRepositoryImp(JPAGroupRepository jpaGroupRepository,
      JPAUserRepository jpaUserRepository) {
    this.jpaGroupRepository = jpaGroupRepository;
    this.jpaUserRepository = jpaUserRepository;
  }

  @Override
  public Group saveGroup(Group group) {
    GroupSchema groupSchema = this.jpaGroupRepository.save(GroupMapper.toSchema(group));

    return GroupMapper.toModel(groupSchema);
  }

  @Override
  public Group getGroupById(String groupId) {
    GroupSchema groupSchema = this.jpaGroupRepository.findById(groupId).orElse(null);

    if (Objects.isNull(groupSchema)) {
      return null;
    }

    return GroupMapper.toModel(groupSchema);
  }

  @Override
  public Group updateGroupById(String groupId, Group group) {
    GroupSchema groupSchema = this.jpaGroupRepository.getReferenceById(groupId);

    groupSchema.setDescription(group.description());
    groupSchema.setName(group.name());

    GroupSchema updatedGroupData = this.jpaGroupRepository.save(groupSchema);
    return GroupMapper.toModel(updatedGroupData);
  }

  @Override
  public Group addMemberToGroup(String groupId, String memberId) {
    GroupSchema groupSchema = this.jpaGroupRepository.findById(groupId).orElse(null);
    UserSchema userSchema = this.jpaUserRepository.findById(memberId).orElse(null);
    if (Objects.isNull(groupSchema) || Objects.isNull(userSchema)) {
      return null;
    }

    Set<UserSchema> members = groupSchema.getMembers();
    if (members.contains(userSchema)) {
      throw new ResourceExistException(EXISTING_FRIEND_IN_THE_GROUP);
    }

    members.add(userSchema);
    GroupSchema updatedGroupSchema = jpaGroupRepository.save(groupSchema);
    return GroupMapper.toModel(updatedGroupSchema);
  }

  @Override
  public Set<Group> getGroupsByUserId(String userId) {
    return this.jpaGroupRepository.findByMembers_Id(userId).orElse(null).stream()
        .map(GroupMapper::toModel).collect(Collectors.toSet());

  }

}
