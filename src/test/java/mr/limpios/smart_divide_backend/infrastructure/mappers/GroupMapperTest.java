package mr.limpios.smart_divide_backend.infrastructure.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import java.util.Set;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import mr.limpios.smart_divide_backend.domain.models.Group;
import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.infrastructure.schemas.GroupSchema;
import mr.limpios.smart_divide_backend.infrastructure.schemas.UserSchema;

class GroupMapperTest {

    @Test
    void toSchema_mapsCorrectly() {
        try (MockedStatic<UserMapper> userMapperMock = Mockito.mockStatic(UserMapper.class)) {
            Group group = Instancio.create(Group.class);
            
            userMapperMock.when(() -> UserMapper.toSchema(any(User.class)))
                .thenAnswer(invocation -> Instancio.create(UserSchema.class));

            GroupSchema result = GroupMapper.toSchema(group);

            assertNotNull(result);
            assertEquals(group.id(), result.getId());
            assertEquals(group.name(), result.getName());
            assertEquals(group.description(), result.getDescription());
            assertEquals(group.type(), result.getType());
            
            assertNotNull(result.getOwner());
            assertEquals(group.members().size(), result.getMembers().size());
            
            userMapperMock.verify(() -> UserMapper.toSchema(any(User.class)), Mockito.atLeastOnce());
        }
    }

    @Test
    void toModel_mapsCorrectly() {
        try (MockedStatic<UserMapper> userMapperMock = Mockito.mockStatic(UserMapper.class)) {
            GroupSchema schema = Instancio.create(GroupSchema.class);

            userMapperMock.when(() -> UserMapper.toModel(any(UserSchema.class)))
                .thenAnswer(invocation -> Instancio.create(User.class));

            Group result = GroupMapper.toModel(schema);

            assertNotNull(result);
            assertEquals(schema.getId(), result.id());
            assertEquals(schema.getName(), result.name());
            assertEquals(schema.getDescription(), result.description());
            assertEquals(schema.getType(), result.type());
            
            assertNotNull(result.owner());
            assertEquals(schema.getMembers().size(), result.members().size());

            userMapperMock.verify(() -> UserMapper.toModel(any(UserSchema.class)), Mockito.atLeastOnce());
        }
    }

    @Test
    void toModelSet_mapsSetCorrectly() {
        try (MockedStatic<UserMapper> userMapperMock = Mockito.mockStatic(UserMapper.class)) {
            int size = 3;
            Set<GroupSchema> schemas = Instancio.ofSet(GroupSchema.class).size(size).create();

            userMapperMock.when(() -> UserMapper.toModel(any(UserSchema.class)))
                .thenAnswer(invocation -> Instancio.create(User.class));

            Set<Group> result = GroupMapper.toModelSet(schemas);

            assertNotNull(result);
            assertEquals(size, result.size());
            
            userMapperMock.verify(() -> UserMapper.toModel(any(UserSchema.class)), Mockito.atLeastOnce());
        }
    }
}