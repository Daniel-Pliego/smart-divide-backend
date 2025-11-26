package mr.limpios.smart_divide_backend.infraestructure.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import mr.limpios.smart_divide_backend.domain.models.ExpenseGroupBalance;
import mr.limpios.smart_divide_backend.domain.models.Group;
import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.infraestructure.schemas.ExpenseGroupBalanceSchema;
import mr.limpios.smart_divide_backend.infraestructure.schemas.GroupSchema;
import mr.limpios.smart_divide_backend.infraestructure.schemas.UserSchema;

class ExpenseGroupBalanceMapperTest {

    @Test
    void toSchema_mapsCorrectly() {
        try (MockedStatic<UserMapper> userMapperMock = Mockito.mockStatic(UserMapper.class);
             MockedStatic<GroupMapper> groupMapperMock = Mockito.mockStatic(GroupMapper.class)) {

            ExpenseGroupBalance model = Instancio.create(ExpenseGroupBalance.class);
            UserSchema userSchema = Instancio.create(UserSchema.class);
            GroupSchema groupSchema = Instancio.create(GroupSchema.class);

            userMapperMock.when(() -> UserMapper.toSchema(any(User.class)))
                .thenReturn(userSchema);
            groupMapperMock.when(() -> GroupMapper.toSchema(any(Group.class)))
                .thenReturn(groupSchema);

            ExpenseGroupBalanceSchema result = ExpenseGroupBalanceMapper.toSchema(model);

            assertNotNull(result);
            assertEquals(model.id(), result.getId());
            assertEquals(model.amount(), result.getAmount());
            assertEquals(userSchema, result.getCreditor());
            assertEquals(userSchema, result.getDebtor());
            assertEquals(groupSchema, result.getGroup());

            userMapperMock.verify(() -> UserMapper.toSchema(any(User.class)), Mockito.times(2));
            groupMapperMock.verify(() -> GroupMapper.toSchema(any(Group.class)));
        }
    }

    @Test
    void toModel_mapsCorrectly() {
        try (MockedStatic<UserMapper> userMapperMock = Mockito.mockStatic(UserMapper.class);
             MockedStatic<GroupMapper> groupMapperMock = Mockito.mockStatic(GroupMapper.class)) {

            ExpenseGroupBalanceSchema schema = Instancio.create(ExpenseGroupBalanceSchema.class);
            User userModel = Instancio.create(User.class);
            Group groupModel = Instancio.create(Group.class);

            userMapperMock.when(() -> UserMapper.toModel(any(UserSchema.class)))
                .thenReturn(userModel);
            groupMapperMock.when(() -> GroupMapper.toModel(any(GroupSchema.class)))
                .thenReturn(groupModel);

            ExpenseGroupBalance model = ExpenseGroupBalanceMapper.toModel(schema);

            assertNotNull(model);
            assertEquals(schema.getId(), model.id());
            assertEquals(schema.getAmount(), model.amount());
            assertEquals(userModel, model.creditor());
            assertEquals(userModel, model.debtor());
            assertEquals(groupModel, model.group());

            userMapperMock.verify(() -> UserMapper.toModel(any(UserSchema.class)), Mockito.times(2));
            groupMapperMock.verify(() -> GroupMapper.toModel(any(GroupSchema.class)));
        }
    }
}