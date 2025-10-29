package mr.limpios.smart_divide_backend.infraestructure.repositories.impl;

import mr.limpios.smart_divide_backend.domain.exceptions.ResourceNotFoundException;
import mr.limpios.smart_divide_backend.infraestructure.repositories.jpa.JPAUserRepository;
import mr.limpios.smart_divide_backend.infraestructure.schemas.UserSchema;
import org.aspectj.apache.bcel.ExceptionConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mr.limpios.smart_divide_backend.aplication.repositories.GroupRepository;
import mr.limpios.smart_divide_backend.domain.models.Group;
import mr.limpios.smart_divide_backend.infraestructure.mappers.GroupMapper;
import mr.limpios.smart_divide_backend.infraestructure.repositories.jpa.JPAGroupRepository;
import mr.limpios.smart_divide_backend.infraestructure.schemas.GroupSchema;

import java.util.Objects;
import java.util.Set;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.EXISTING_FRIEND_IN_THE_GROUP;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.FRIENDSHIP_ALREADY_EXISTS;

@Repository
public class GroupRepositoryImp implements GroupRepository {

  private JPAGroupRepository jpaGroupRepository;
  private JPAUserRepository jpaUserRepository;

  @Autowired
  public GroupRepositoryImp(JPAGroupRepository jpaGroupRepository, JPAUserRepository jpaUserRepository) {
      this.jpaGroupRepository = jpaGroupRepository;
      this.jpaUserRepository = jpaUserRepository; // <-- Ahora jpaUserRepository será inicializado
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
        throw new ResourceNotFoundException(EXISTING_FRIEND_IN_THE_GROUP);
    }

    members.add(userSchema);
    GroupSchema updatedGroupSchema = jpaGroupRepository.save(groupSchema);
    return GroupMapper.toModel(updatedGroupSchema);
  }
}
