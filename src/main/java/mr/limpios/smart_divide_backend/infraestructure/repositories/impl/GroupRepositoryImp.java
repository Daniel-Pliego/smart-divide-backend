package mr.limpios.smart_divide_backend.infraestructure.repositories.impl;

import mr.limpios.smart_divide_backend.aplication.repositories.GroupRepository;
import mr.limpios.smart_divide_backend.domain.models.Group;
import mr.limpios.smart_divide_backend.infraestructure.mappers.GroupMapper;
import mr.limpios.smart_divide_backend.infraestructure.repositories.jpa.JPAGroupRepository;
import mr.limpios.smart_divide_backend.infraestructure.schemas.GroupSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class GroupRepositoryImp implements GroupRepository {
  @Autowired
  private JPAGroupRepository jpaGroupRepository;

  @Override
  public Group saveGroup(Group group) {
    GroupSchema groupSchema = this.jpaGroupRepository.save(GroupMapper.toSchema(group));

    return GroupMapper.toModel(groupSchema);
  }
}
