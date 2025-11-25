package mr.limpios.smart_divide_backend.infraestructure.mappers;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.util.Collections;
import java.util.List;

import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import mr.limpios.smart_divide_backend.domain.models.Expense;
import mr.limpios.smart_divide_backend.infraestructure.schemas.ExpenseSchema;
import mr.limpios.smart_divide_backend.infraestructure.schemas.GroupSchema;
import mr.limpios.smart_divide_backend.infraestructure.schemas.ExpenseParticipantSchema;
import mr.limpios.smart_divide_backend.infraestructure.schemas.ExpenseBalanceSchema;

@DisplayName("ExpenseMapper Tests")
class ExpenseMapperTest {

    @Test
    @DisplayName("toSchema - Maps all fields correctly including sub-mappers")
    void toSchema_mapsCorrectly() {
        try (MockedStatic<GroupMapper> groupMapperMock = Mockito.mockStatic(GroupMapper.class);
             MockedStatic<ExpenseParticipantMapper> participantMapperMock = Mockito.mockStatic(ExpenseParticipantMapper.class);
             MockedStatic<ExpenseBalanceMapper> balanceMapperMock = Mockito.mockStatic(ExpenseBalanceMapper.class)) {

            Expense expense = Instancio.create(Expense.class);
            
            GroupSchema groupSchema = Instancio.create(GroupSchema.class);
            List<ExpenseParticipantSchema> participantSchemas = Collections.emptyList();
            List<ExpenseBalanceSchema> balanceSchemas = Collections.emptyList();

            groupMapperMock.when(() -> GroupMapper.toSchema(expense.group()))
                .thenReturn(groupSchema);

            participantMapperMock.when(() -> ExpenseParticipantMapper.toSchemaList(eq(expense.participants()), any(ExpenseSchema.class)))
                .thenReturn(participantSchemas);
                
            balanceMapperMock.when(() -> ExpenseBalanceMapper.toSchemaList(eq(expense.balances()), any(ExpenseSchema.class)))
                .thenReturn(balanceSchemas);

            ExpenseSchema result = ExpenseMapper.toSchema(expense);

            assertNotNull(result);
            assertEquals(expense.id(), result.getId());
            assertEquals(expense.type(), result.getType());
            assertEquals(expense.description(), result.getDescription());
            assertEquals(expense.amount(), result.getAmount());
            assertEquals(expense.evidenceUrl(), result.getEvidenceUrl());
            assertEquals(expense.createdAt(), result.getCreatedAt());
            assertEquals(expense.divisionType(), result.getDivisionType());
            
            assertEquals(groupSchema, result.getGroup());
            assertEquals(participantSchemas, result.getParticipants());
            assertEquals(balanceSchemas, result.getBalances());
        }
    }

    @Test
    @DisplayName("toSchema - Handles null createdAt safely")
    void toSchema_handlesNullCreatedAt() {
        try (MockedStatic<GroupMapper> groupMapperMock = Mockito.mockStatic(GroupMapper.class);
             MockedStatic<ExpenseParticipantMapper> participantMapperMock = Mockito.mockStatic(ExpenseParticipantMapper.class);
             MockedStatic<ExpenseBalanceMapper> balanceMapperMock = Mockito.mockStatic(ExpenseBalanceMapper.class)) {

            Expense expense = Instancio.of(Expense.class)
                .set(field("createdAt"), null)
                .create();

            ExpenseSchema result = ExpenseMapper.toSchema(expense);

            assertNotNull(result.getCreatedAt(), "CreatedAt should be null in schema if model is null");
            
            assertEquals(expense.id(), result.getId());
        }
    }

    @Test
    @DisplayName("toModel - Maps all fields correctly including sub-mappers")
    void toModel_mapsCorrectly() {
        try (MockedStatic<GroupMapper> groupMapperMock = Mockito.mockStatic(GroupMapper.class);
             MockedStatic<ExpenseParticipantMapper> participantMapperMock = Mockito.mockStatic(ExpenseParticipantMapper.class);
             MockedStatic<ExpenseBalanceMapper> balanceMapperMock = Mockito.mockStatic(ExpenseBalanceMapper.class)) {

            ExpenseSchema schema = Instancio.create(ExpenseSchema.class);

            Expense result = ExpenseMapper.toModel(schema);

            assertNotNull(result);
            assertEquals(schema.getId(), result.id());
            assertEquals(schema.getType(), result.type());
            assertEquals(schema.getDescription(), result.description());
            assertEquals(schema.getAmount(), result.amount());
            assertEquals(schema.getEvidenceUrl(), result.evidenceUrl());
            assertEquals(schema.getCreatedAt(), result.createdAt());
            assertEquals(schema.getDivisionType(), result.divisionType());

            groupMapperMock.verify(() -> GroupMapper.toModel(schema.getGroup()));
            participantMapperMock.verify(() -> ExpenseParticipantMapper.toModelList(schema.getParticipants()));
            balanceMapperMock.verify(() -> ExpenseBalanceMapper.toModelList(schema.getBalances()));
        }
    }
}