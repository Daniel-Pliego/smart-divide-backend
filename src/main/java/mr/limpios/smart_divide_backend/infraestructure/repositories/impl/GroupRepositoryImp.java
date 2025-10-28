package mr.limpios.smart_divide_backend.infraestructure.repositories.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mr.limpios.smart_divide_backend.aplication.repositories.GroupRepository;
import mr.limpios.smart_divide_backend.domain.models.Group;
import mr.limpios.smart_divide_backend.infraestructure.mappers.GroupMapper;
import mr.limpios.smart_divide_backend.infraestructure.repositories.jpa.JPAGroupRepository;
import mr.limpios.smart_divide_backend.infraestructure.schemas.GroupSchema;

import java.util.Objects;

@Repository
public class GroupRepositoryImp implements GroupRepository {
  @Autowired
  private JPAGroupRepository jpaGroupRepository;

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
}
