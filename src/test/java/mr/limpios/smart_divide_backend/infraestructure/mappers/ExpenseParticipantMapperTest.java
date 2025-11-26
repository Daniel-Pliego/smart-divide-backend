package mr.limpios.smart_divide_backend.infraestructure.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import java.util.List;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import mr.limpios.smart_divide_backend.domain.models.ExpenseParticipant;
import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.infraestructure.schemas.ExpenseParticipantSchema;
import mr.limpios.smart_divide_backend.infraestructure.schemas.ExpenseSchema;
import mr.limpios.smart_divide_backend.infraestructure.schemas.UserSchema;

class ExpenseParticipantMapperTest {

    @Test
    void toSchema_mapsCorrectly() {
        try (MockedStatic<UserMapper> userMapperMock = Mockito.mockStatic(UserMapper.class)) {
            ExpenseParticipant participant = Instancio.create(ExpenseParticipant.class);
            ExpenseSchema expenseSchema = Instancio.create(ExpenseSchema.class);
            UserSchema userSchema = Instancio.create(UserSchema.class);

            userMapperMock.when(() -> UserMapper.toSchema(any(User.class)))
                .thenReturn(userSchema);

            ExpenseParticipantSchema result = ExpenseParticipantMapper.toSchema(participant, expenseSchema);

            assertNotNull(result);
            assertEquals(participant.id(), result.getId());
            assertEquals(participant.amountPaid(), result.getAmountPaid());
            assertEquals(participant.mustPaid(), result.getMustPaid());
            assertEquals(expenseSchema, result.getExpense());
            assertEquals(userSchema, result.getPayer());

            userMapperMock.verify(() -> UserMapper.toSchema(any(User.class)));
        }
    }

    @Test
    void toModel_mapsCorrectly() {
        try (MockedStatic<UserMapper> userMapperMock = Mockito.mockStatic(UserMapper.class)) {
            ExpenseParticipantSchema schema = Instancio.create(ExpenseParticipantSchema.class);
            User user = Instancio.create(User.class);

            userMapperMock.when(() -> UserMapper.toModel(any(UserSchema.class)))
                .thenReturn(user);

            ExpenseParticipant result = ExpenseParticipantMapper.toModel(schema);

            assertNotNull(result);
            assertEquals(schema.getId(), result.id());
            assertEquals(schema.getAmountPaid(), result.amountPaid());
            assertEquals(schema.getMustPaid(), result.mustPaid());
            assertEquals(user, result.payer());

            userMapperMock.verify(() -> UserMapper.toModel(any(UserSchema.class)));
        }
    }

    @Test
    void toSchemaList_mapsListCorrectly() {
        try (MockedStatic<UserMapper> userMapperMock = Mockito.mockStatic(UserMapper.class)) {
            int size = 3;
            List<ExpenseParticipant> participants = Instancio.ofList(ExpenseParticipant.class)
                .size(size)
                .create();
            ExpenseSchema expenseSchema = Instancio.create(ExpenseSchema.class);
            UserSchema userSchema = Instancio.create(UserSchema.class);

            userMapperMock.when(() -> UserMapper.toSchema(any(User.class)))
                .thenReturn(userSchema);

            List<ExpenseParticipantSchema> results = ExpenseParticipantMapper.toSchemaList(participants, expenseSchema);

            assertNotNull(results);
            assertEquals(size, results.size());
            assertEquals(participants.get(0).id(), results.get(0).getId());
            assertEquals(expenseSchema, results.get(0).getExpense());
        }
    }

    @Test
    void toModelList_mapsListCorrectly() {
        try (MockedStatic<UserMapper> userMapperMock = Mockito.mockStatic(UserMapper.class)) {
            int size = 3;
            List<ExpenseParticipantSchema> schemas = Instancio.ofList(ExpenseParticipantSchema.class)
                .size(size)
                .create();
            User user = Instancio.create(User.class);

            userMapperMock.when(() -> UserMapper.toModel(any(UserSchema.class)))
                .thenReturn(user);

            List<ExpenseParticipant> results = ExpenseParticipantMapper.toModelList(schemas);

            assertNotNull(results);
            assertEquals(size, results.size());
            assertEquals(schemas.get(0).getId(), results.get(0).id());
            assertEquals(user, results.get(0).payer());
        }
    }
}