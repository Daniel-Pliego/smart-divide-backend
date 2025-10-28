package mr.limpios.smart_divide_backend.aplication.repositories;

import mr.limpios.smart_divide_backend.domain.models.Group;

public interface GroupRepository {

  Group saveGroup(Group group);
  Group getGroupById(String groupId);
  Group updateGroupById(String groupId, Group group);
}
