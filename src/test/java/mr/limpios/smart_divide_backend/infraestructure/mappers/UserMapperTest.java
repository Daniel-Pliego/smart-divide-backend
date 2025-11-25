package mr.limpios.smart_divide_backend.infraestructure.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import mr.limpios.smart_divide_backend.domain.models.Card;
import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.infraestructure.schemas.CardSchema;
import mr.limpios.smart_divide_backend.infraestructure.schemas.UserSchema;

class UserMapperTest {

    @Test
    void toSchema_mapsCorrectly() {
        try (MockedStatic<CardMapper> cardMapperMock = Mockito.mockStatic(CardMapper.class)) {
            User user = Instancio.create(User.class);

            cardMapperMock.when(() -> CardMapper.toSchema(any(Card.class)))
                .thenAnswer(invocation -> Instancio.create(CardSchema.class));

            UserSchema result = UserMapper.toSchema(user);

            assertNotNull(result);
            assertEquals(user.id(), result.getId());
            assertEquals(user.name(), result.getName());
            assertEquals(user.lastName(), result.getLastName());
            assertEquals(user.email(), result.getEmail());
            assertEquals(user.password(), result.getPassword());
            assertEquals(user.photoUrl(), result.getPhotoUrl());
            assertEquals(user.isVerified(), result.getIsVerified());
            assertEquals(user.cards().size(), result.getCards().size());
            
            cardMapperMock.verify(() -> CardMapper.toSchema(any(Card.class)), Mockito.atLeastOnce());
        }
    }

    @Test
    void toSchema_handlesNullCards() {
        User user = Instancio.of(User.class)
            .set(Select.field(User.class, "cards"), null)
            .create();

        UserSchema result = UserMapper.toSchema(user);

        assertNotNull(result);
        assertEquals(user.id(), result.getId());
        assertNotNull(result.getCards());
        assertTrue(result.getCards().isEmpty());
    }

    @Test
    void toModel_mapsCorrectly() {
        try (MockedStatic<CardMapper> cardMapperMock = Mockito.mockStatic(CardMapper.class)) {
            UserSchema schema = Instancio.create(UserSchema.class);
            Card cardModel = Instancio.create(Card.class);

            cardMapperMock.when(() -> CardMapper.toModel(any(CardSchema.class)))
                .thenReturn(cardModel);

            User result = UserMapper.toModel(schema);

            assertNotNull(result);
            assertEquals(schema.getId(), result.id());
            assertEquals(schema.getName(), result.name());
            assertEquals(schema.getLastName(), result.lastName());
            assertEquals(schema.getEmail(), result.email());
            assertEquals(schema.getPassword(), result.password());
            assertEquals(schema.getPhotoUrl(), result.photoUrl());
            assertEquals(schema.getIsVerified(), result.isVerified());
            assertEquals(schema.getCards().size(), result.cards().size());

            cardMapperMock.verify(() -> CardMapper.toModel(any(CardSchema.class)), Mockito.atLeastOnce());
        }
    }

    @Test
    void toModel_handlesNullCards() {
        UserSchema schema = Instancio.of(UserSchema.class)
            .set(Select.field(UserSchema.class, "cards"), null)
            .create();

        User result = UserMapper.toModel(schema);

        assertNotNull(result);
        assertEquals(schema.getId(), result.id());
        assertNotNull(result.cards());
        assertTrue(result.cards().isEmpty());
    }
}