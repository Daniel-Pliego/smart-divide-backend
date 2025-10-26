package mr.limpios.smart_divide_backend.infraestructure.mappers;

import mr.limpios.smart_divide_backend.domain.models.Group;
import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.infraestructure.schemas.GroupSchema;
import mr.limpios.smart_divide_backend.infraestructure.schemas.UserSchema;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupMapper {

    private GroupMapper(){}

    public static GroupSchema toSchema(Group group) {

        Set<UserSchema> membersSchemas = group.members()
                .stream().map(UserMapper::toSchema)
                .collect(Collectors.toSet());

        return new GroupSchema(
                group.id(),
                group.name(),
                group.description(),
                GroupIconMapper.toSchema(group.groupIcon()),
                UserMapper.toSchema(group.owner()),
                membersSchemas
                );
    }

    public static Group toModel(GroupSchema groupSchema) {
        List<User> membersModels = groupSchema.getMembers()
                .stream().map(UserMapper::toModel)
                .collect(Collectors.toList());

        return new Group(
                groupSchema.getId(),
                groupSchema.getName(),
                groupSchema.getDescription(),
                GroupIconMapper.toModel(groupSchema.getGroupIcon()),
                UserMapper.toModel(groupSchema.getOwner()),
                membersModels
        );
    }
}
