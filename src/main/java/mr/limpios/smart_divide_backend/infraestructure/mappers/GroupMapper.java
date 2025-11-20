package mr.limpios.smart_divide_backend.infraestructure.mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import mr.limpios.smart_divide_backend.domain.models.Group;
import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.infraestructure.schemas.GroupSchema;
import mr.limpios.smart_divide_backend.infraestructure.schemas.UserSchema;

public class GroupMapper {

  private GroupMapper() {}

  public static GroupSchema toSchema(Group group) {

    Set<UserSchema> membersSchemas =
        group.members().stream().map(UserMapper::toSchema).collect(Collectors.toSet());

    return new GroupSchema(group.id(), group.name(), group.description(),
        UserMapper.toSchema(group.owner()), group.type(), membersSchemas);
  }

  public static Group toModel(GroupSchema groupSchema) {
    List<User> membersModels =
        groupSchema.getMembers().stream().map(UserMapper::toModel).collect(Collectors.toList());

    return new Group(groupSchema.getId(), groupSchema.getName(), groupSchema.getDescription(),
        UserMapper.toModel(groupSchema.getOwner()), groupSchema.getType(), membersModels);
  }

  public static Set<Group> toModelSet(Set<GroupSchema> groupsSchema) {
    return groupsSchema.stream().map(GroupMapper::toModel).collect(Collectors.toSet());
  }
}
