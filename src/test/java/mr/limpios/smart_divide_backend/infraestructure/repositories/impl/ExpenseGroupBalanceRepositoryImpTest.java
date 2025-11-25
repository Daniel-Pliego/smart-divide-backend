package mr.limpios.smart_divide_backend.infraestructure.repositories.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import mr.limpios.smart_divide_backend.domain.models.ExpenseGroupBalance;
import mr.limpios.smart_divide_backend.infraestructure.mappers.ExpenseGroupBalanceMapper;
import mr.limpios.smart_divide_backend.infraestructure.repositories.jpa.JPAExpenseGroupBalanceRepository;
import mr.limpios.smart_divide_backend.infraestructure.schemas.ExpenseGroupBalanceSchema;

@ExtendWith(MockitoExtension.class)
class ExpenseGroupBalanceRepositoryImpTest {

    @Mock
    private JPAExpenseGroupBalanceRepository jpaRepository;

    @InjectMocks
    private ExpenseGroupBalanceRepositoryImp repository;

    @Test
    void saveExpenseGroupBalance_success() {
        try (MockedStatic<ExpenseGroupBalanceMapper> mapperMock = Mockito.mockStatic(ExpenseGroupBalanceMapper.class)) {
            ExpenseGroupBalance inputModel = Instancio.create(ExpenseGroupBalance.class);
            ExpenseGroupBalanceSchema schema = Instancio.create(ExpenseGroupBalanceSchema.class);
            ExpenseGroupBalanceSchema savedSchema = Instancio.create(ExpenseGroupBalanceSchema.class);
            ExpenseGroupBalance outputModel = Instancio.create(ExpenseGroupBalance.class);

            mapperMock.when(() -> ExpenseGroupBalanceMapper.toSchema(inputModel)).thenReturn(schema);
            when(jpaRepository.save(schema)).thenReturn(savedSchema);
            mapperMock.when(() -> ExpenseGroupBalanceMapper.toModel(savedSchema)).thenReturn(outputModel);

            ExpenseGroupBalance result = repository.saveExpenseGroupBalance(inputModel);

            assertNotNull(result);
            assertEquals(outputModel, result);
            verify(jpaRepository).save(schema);
        }
    }

    @Test
    void findByCreditorAndDebtorAndGroup_found() {
        try (MockedStatic<ExpenseGroupBalanceMapper> mapperMock = Mockito.mockStatic(ExpenseGroupBalanceMapper.class)) {
            String creditorId = "c1";
            String debtorId = "d1";
            String groupId = "g1";
            ExpenseGroupBalanceSchema schema = Instancio.create(ExpenseGroupBalanceSchema.class);
            ExpenseGroupBalance model = Instancio.create(ExpenseGroupBalance.class);

            when(jpaRepository.findByCreditor_IdAndDebtor_IdAndGroup_Id(creditorId, debtorId, groupId))
                .thenReturn(Optional.of(schema));
            mapperMock.when(() -> ExpenseGroupBalanceMapper.toModel(schema)).thenReturn(model);

            Optional<ExpenseGroupBalance> result = repository.findByCreditorAndDebtorAndGroup(creditorId, debtorId, groupId);

            assertTrue(result.isPresent());
            assertEquals(model, result.get());
        }
    }

    @Test
    void findByCreditorAndDebtorAndGroup_notFound() {
        String creditorId = "c1";
        String debtorId = "d1";
        String groupId = "g1";

        when(jpaRepository.findByCreditor_IdAndDebtor_IdAndGroup_Id(creditorId, debtorId, groupId))
            .thenReturn(Optional.empty());

        Optional<ExpenseGroupBalance> result = repository.findByCreditorAndDebtorAndGroup(creditorId, debtorId, groupId);

        assertTrue(result.isEmpty());
    }

    @Test
    void findByGroupIdAndCreditorId_success() {
        try (MockedStatic<ExpenseGroupBalanceMapper> mapperMock = Mockito.mockStatic(ExpenseGroupBalanceMapper.class)) {
            String groupId = "g1";
            String creditorId = "c1";
            List<ExpenseGroupBalanceSchema> schemas = Instancio.ofList(ExpenseGroupBalanceSchema.class).size(2).create();
            ExpenseGroupBalance model = Instancio.create(ExpenseGroupBalance.class);

            when(jpaRepository.findByGroupIdAndCreditorId(groupId, creditorId)).thenReturn(schemas);
            mapperMock.when(() -> ExpenseGroupBalanceMapper.toModel(any(ExpenseGroupBalanceSchema.class))).thenReturn(model);

            List<ExpenseGroupBalance> result = repository.findByGroupIdAndCreditorId(groupId, creditorId);

            assertNotNull(result);
            assertEquals(2, result.size());
        }
    }

    @Test
    void findByGroupIdAndDebtorId_success() {
        try (MockedStatic<ExpenseGroupBalanceMapper> mapperMock = Mockito.mockStatic(ExpenseGroupBalanceMapper.class)) {
            String groupId = "g1";
            String debtorId = "d1";
            List<ExpenseGroupBalanceSchema> schemas = Instancio.ofList(ExpenseGroupBalanceSchema.class).size(2).create();
            ExpenseGroupBalance model = Instancio.create(ExpenseGroupBalance.class);

            when(jpaRepository.findByGroupIdAndDebtorId(groupId, debtorId)).thenReturn(schemas);
            mapperMock.when(() -> ExpenseGroupBalanceMapper.toModel(any(ExpenseGroupBalanceSchema.class))).thenReturn(model);

            List<ExpenseGroupBalance> result = repository.findByGroupIdAndDebtorId(groupId, debtorId);

            assertNotNull(result);
            assertEquals(2, result.size());
        }
    }

    @Test
    void deleteExpenseGroupBalance_success() {
        Integer id = 123;
        repository.deleteExpenseGroupBalance(id);
        verify(jpaRepository).deleteById(id);
    }

    @Test
    void getTotalDebtsByGroupAndDebtor_success() {
        String groupId = "g1";
        String userId = "u1";
        BigDecimal expected = new BigDecimal("100.50");

        when(jpaRepository.sumDebtsByGroupAndDebtor(groupId, userId)).thenReturn(expected);

        BigDecimal result = repository.getTotalDebtsByGroupAndDebtor(groupId, userId);

        assertEquals(expected, result);
    }

    @Test
    void getTotalCreditsByGroupAndDebtor_success() {
        String groupId = "g1";
        String userId = "u1";
        BigDecimal expected = new BigDecimal("200.00");

        when(jpaRepository.sumCreditsByGroupAndDebtor(groupId, userId)).thenReturn(expected);

        BigDecimal result = repository.getTotalCreditsByGroupAndDebtor(groupId, userId);

        assertEquals(expected, result);
    }

    @Test
    void findAllByGroup_success() {
        try (MockedStatic<ExpenseGroupBalanceMapper> mapperMock = Mockito.mockStatic(ExpenseGroupBalanceMapper.class)) {
            String groupId = "g1";
            List<ExpenseGroupBalanceSchema> schemas = Instancio.ofList(ExpenseGroupBalanceSchema.class).size(3).create();
            ExpenseGroupBalance model = Instancio.create(ExpenseGroupBalance.class);

            when(jpaRepository.findAllByGroupId(groupId)).thenReturn(schemas);
            mapperMock.when(() -> ExpenseGroupBalanceMapper.toModel(any(ExpenseGroupBalanceSchema.class))).thenReturn(model);

            List<ExpenseGroupBalance> result = repository.findAllByGroup(groupId);

            assertNotNull(result);
            assertEquals(3, result.size());
        }
    }
}