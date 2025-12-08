package mr.limpios.smart_divide_backend.application.repositories;

import java.util.Set;

import mr.limpios.smart_divide_backend.domain.models.Group;

public interface GroupRepository {

  Group saveGroup(Group group);

  Group getGroupById(String groupId);

  Group updateGroupById(String groupId, Group group);

  Group addMemberToGroup(String groupId, String memberId);

  Set<Group> getGroupsByUserId(String userId);
}
