package mr.limpios.smart_divide_backend.infrastructure.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import mr.limpios.smart_divide_backend.domain.models.Friendship;
import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.infrastructure.schemas.FriendshipSchema;
import mr.limpios.smart_divide_backend.infrastructure.schemas.UserSchema;

class FriendshipMapperTest {

    @Test
    void toSchema_mapsCorrectly() {
        try (MockedStatic<UserMapper> userMapperMock = Mockito.mockStatic(UserMapper.class)) {
            Friendship model = Instancio.create(Friendship.class);
            UserSchema userSchema = Instancio.create(UserSchema.class);

            userMapperMock.when(() -> UserMapper.toSchema(any(User.class)))
                .thenReturn(userSchema);

            FriendshipSchema result = FriendshipMapper.toSchema(model);

            assertNotNull(result);
            assertEquals(model.id(), result.getId());
            assertEquals(model.confirmed(), result.getConfirmed());
            assertEquals(userSchema, result.getRequester());
            assertEquals(userSchema, result.getFriend());

            userMapperMock.verify(() -> UserMapper.toSchema(any(User.class)), Mockito.times(2));
        }
    }

    @Test
    void toModel_mapsCorrectly() {
        try (MockedStatic<UserMapper> userMapperMock = Mockito.mockStatic(UserMapper.class)) {
            FriendshipSchema schema = Instancio.create(FriendshipSchema.class);
            User userModel = Instancio.create(User.class);

            userMapperMock.when(() -> UserMapper.toModel(any(UserSchema.class)))
                .thenReturn(userModel);

            Friendship result = FriendshipMapper.toModel(schema);

            assertNotNull(result);
            assertEquals(schema.getId(), result.id());
            assertEquals(schema.getConfirmed(), result.confirmed());
            assertEquals(userModel, result.requester());
            assertEquals(userModel, result.friend());

            userMapperMock.verify(() -> UserMapper.toModel(any(UserSchema.class)), Mockito.times(2));
        }
    }
}