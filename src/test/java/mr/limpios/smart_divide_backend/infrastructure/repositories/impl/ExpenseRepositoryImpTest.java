package mr.limpios.smart_divide_backend.infrastructure.repositories.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import mr.limpios.smart_divide_backend.domain.models.Expense;
import mr.limpios.smart_divide_backend.infrastructure.mappers.ExpenseMapper;
import mr.limpios.smart_divide_backend.infrastructure.repositories.jpa.JPAExpenseRepository;
import mr.limpios.smart_divide_backend.infrastructure.schemas.ExpenseSchema;

@ExtendWith(MockitoExtension.class)
class ExpenseRepositoryImpTest {

    @Mock
    private JPAExpenseRepository jpaExpenseRepository;

    @InjectMocks
    private ExpenseRepositoryImp expenseRepository;

    @Test
    void saveExpense_success() {
        try (MockedStatic<ExpenseMapper> mapperMock = Mockito.mockStatic(ExpenseMapper.class)) {
            Expense expense = Instancio.create(Expense.class);
            ExpenseSchema schema = Instancio.create(ExpenseSchema.class);
            ExpenseSchema savedSchema = Instancio.create(ExpenseSchema.class);
            Expense savedExpense = Instancio.create(Expense.class);

            mapperMock.when(() -> ExpenseMapper.toSchema(expense)).thenReturn(schema);
            when(jpaExpenseRepository.save(schema)).thenReturn(savedSchema);
            mapperMock.when(() -> ExpenseMapper.toModel(savedSchema)).thenReturn(savedExpense);

            Expense result = expenseRepository.saveExpense(expense);

            assertNotNull(result);
            assertEquals(savedExpense, result);
            verify(jpaExpenseRepository).save(schema);
        }
    }

    @Test
    void findByGroupId_success() {
        try (MockedStatic<ExpenseMapper> mapperMock = Mockito.mockStatic(ExpenseMapper.class)) {
            String groupId = "group-1";
            int listSize = 3;
            List<ExpenseSchema> schemas = Instancio.ofList(ExpenseSchema.class).size(listSize).create();

            when(jpaExpenseRepository.findByGroupId(groupId)).thenReturn(schemas);
            
            mapperMock.when(() -> ExpenseMapper.toModel(any(ExpenseSchema.class)))
                .thenAnswer(invocation -> Instancio.create(Expense.class));

            List<Expense> result = expenseRepository.findByGroupId(groupId);

            assertNotNull(result);
            assertEquals(listSize, result.size());
            verify(jpaExpenseRepository).findByGroupId(groupId);
        }
    }
}
