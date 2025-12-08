package mr.limpios.smart_divide_backend.infrastructure.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.infrastructure.schemas.UserSchema;

class UserMapperTest {

    @Test
    void toSchema_mapsCorrectly() {
        User user = Instancio.create(User.class);

        UserSchema result = UserMapper.toSchema(user);

        assertNotNull(result);
        assertEquals(user.id(), result.getId());
        assertEquals(user.name(), result.getName());
        assertEquals(user.lastName(), result.getLastName());
        assertEquals(user.email(), result.getEmail());
        assertEquals(user.password(), result.getPassword());
        assertEquals(user.photoUrl(), result.getPhotoUrl());
        assertEquals(user.isVerified(), result.getIsVerified());

    }

    @Test
    void toModel_mapsCorrectly() {

        UserSchema schema = Instancio.create(UserSchema.class);

        User result = UserMapper.toModel(schema);

        assertNotNull(result);
        assertEquals(schema.getId(), result.id());
        assertEquals(schema.getName(), result.name());
        assertEquals(schema.getLastName(), result.lastName());
        assertEquals(schema.getEmail(), result.email());
        assertEquals(schema.getPassword(), result.password());
        assertEquals(schema.getPhotoUrl(), result.photoUrl());
        assertEquals(schema.getIsVerified(), result.isVerified());

    }
}