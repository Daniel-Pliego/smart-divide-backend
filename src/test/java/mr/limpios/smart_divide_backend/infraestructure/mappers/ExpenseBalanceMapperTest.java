package mr.limpios.smart_divide_backend.infraestructure.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import java.util.List;

import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import mr.limpios.smart_divide_backend.domain.models.ExpenseBalance;
import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.infraestructure.schemas.ExpenseBalanceSchema;
import mr.limpios.smart_divide_backend.infraestructure.schemas.ExpenseSchema;
import mr.limpios.smart_divide_backend.infraestructure.schemas.UserSchema;

class ExpenseBalanceMapperTest {

    @Test
    @DisplayName("toSchema - Maps single balance correctly using UserMapper")
    void toSchema_mapsCorrectly() {
        try (MockedStatic<UserMapper> userMapperMock = Mockito.mockStatic(UserMapper.class)) {
            ExpenseBalance balance = Instancio.create(ExpenseBalance.class);
            ExpenseSchema expenseSchema = Instancio.create(ExpenseSchema.class);
            UserSchema userSchemaMock = Instancio.create(UserSchema.class);

            userMapperMock.when(() -> UserMapper.toSchema(any(User.class)))
                .thenReturn(userSchemaMock);

            ExpenseBalanceSchema result = ExpenseBalanceMapper.toSchema(balance, expenseSchema);

            assertNotNull(result);
            assertEquals(balance.id(), result.getId());
            assertEquals(balance.amountToPaid(), result.getAmountToPaid());
            assertEquals(expenseSchema, result.getExpense());
            
            assertEquals(userSchemaMock, result.getCreditor());
            assertEquals(userSchemaMock, result.getDebtor());
            
            userMapperMock.verify(() -> UserMapper.toSchema(any(User.class)), Mockito.times(2));
        }
    }

    @Test
    @DisplayName("toModel - Maps single schema correctly using UserMapper")
    void toModel_mapsCorrectly() {
        try (MockedStatic<UserMapper> userMapperMock = Mockito.mockStatic(UserMapper.class)) {
            ExpenseBalanceSchema schema = Instancio.create(ExpenseBalanceSchema.class);
            User userMock = Instancio.create(User.class);

            userMapperMock.when(() -> UserMapper.toModel(any(UserSchema.class)))
                .thenReturn(userMock);

            ExpenseBalance result = ExpenseBalanceMapper.toModel(schema);

            assertNotNull(result);
            assertEquals(schema.getId(), result.id());
            assertEquals(schema.getAmountToPaid(), result.amountToPaid());
            
            assertEquals(userMock, result.creditor());
            assertEquals(userMock, result.debtor());
        }
    }

    @Test
    @DisplayName("toSchemaList - Maps list of balances correctly")
    void toSchemaList_mapsListCorrectly() {
        try (MockedStatic<UserMapper> userMapperMock = Mockito.mockStatic(UserMapper.class)) {
            int listSize = 3;
            List<ExpenseBalance> balances = Instancio.ofList(ExpenseBalance.class)
                .size(listSize)
                .create();
            ExpenseSchema expenseSchema = Instancio.create(ExpenseSchema.class);
            UserSchema userSchemaMock = Instancio.create(UserSchema.class);

            userMapperMock.when(() -> UserMapper.toSchema(any(User.class)))
                .thenReturn(userSchemaMock);

            List<ExpenseBalanceSchema> results = ExpenseBalanceMapper.toSchemaList(balances, expenseSchema);

            assertNotNull(results);
            assertEquals(listSize, results.size());
            
            assertEquals(balances.get(0).id(), results.get(0).getId());
            assertEquals(expenseSchema, results.get(0).getExpense());
        }
    }

    @Test
    @DisplayName("toModelList - Maps list of schemas correctly")
    void toModelList_mapsListCorrectly() {
        try (MockedStatic<UserMapper> userMapperMock = Mockito.mockStatic(UserMapper.class)) {
            int listSize = 2;
            List<ExpenseBalanceSchema> schemas = Instancio.ofList(ExpenseBalanceSchema.class)
                .size(listSize)
                .create();
            User userMock = Instancio.create(User.class);

            userMapperMock.when(() -> UserMapper.toModel(any(UserSchema.class)))
                .thenReturn(userMock);

            List<ExpenseBalance> results = ExpenseBalanceMapper.toModelList(schemas);

            assertNotNull(results);
            assertEquals(listSize, results.size());
            assertEquals(schemas.get(0).getId(), results.get(0).id());
            assertEquals(userMock, results.get(0).creditor());
        }
    }
}