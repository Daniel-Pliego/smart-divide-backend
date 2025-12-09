package mr.limpios.smart_divide_backend.application.validators.strategies;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.DEBTORS_AMOUNT_MISMATCH;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.EQUAL_DIVISION_AMOUNT_MISMATCH;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import mr.limpios.smart_divide_backend.application.dtos.CreateExpenseParticipantDTO;
import mr.limpios.smart_divide_backend.application.dtos.ExpenseInputDTO;
import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;

class EqualDivisionValidationStrategyTest {

    private final EqualDivisionValidationStrategy strategy = new EqualDivisionValidationStrategy();

    @Test
    void validate_success_exactDivision() {
        CreateExpenseParticipantDTO p1 = new CreateExpenseParticipantDTO("u1", 50.00);
        CreateExpenseParticipantDTO p2 = new CreateExpenseParticipantDTO("u2", 50.00);
        
        CreateExpenseParticipantDTO payer = new CreateExpenseParticipantDTO("u1", 100.00);

        ExpenseInputDTO dto = Instancio.of(ExpenseInputDTO.class)
            .set(org.instancio.Select.field(ExpenseInputDTO::amount), 100.00)
            .set(org.instancio.Select.field(ExpenseInputDTO::participants), List.of(p1, p2))
            .set(org.instancio.Select.field(ExpenseInputDTO::payers), List.of(payer))
            .create();

        assertDoesNotThrow(() -> strategy.validate(dto));
    }

    @Test
    void validate_success_withRoundingCents() {
        CreateExpenseParticipantDTO p1 = new CreateExpenseParticipantDTO("u1", 33.33);
        CreateExpenseParticipantDTO p2 = new CreateExpenseParticipantDTO("u2", 33.33);
        CreateExpenseParticipantDTO p3 = new CreateExpenseParticipantDTO("u3", 33.34);
        
        CreateExpenseParticipantDTO payer = new CreateExpenseParticipantDTO("u1", 100.00);

        ExpenseInputDTO dto = Instancio.of(ExpenseInputDTO.class)
            .set(org.instancio.Select.field(ExpenseInputDTO::amount), 100.00)
            .set(org.instancio.Select.field(ExpenseInputDTO::participants), List.of(p1, p2, p3))
            .set(org.instancio.Select.field(ExpenseInputDTO::payers), List.of(payer))
            .create();

        assertDoesNotThrow(() -> strategy.validate(dto));
    }

    @Test
    void validate_fail_totalAmountMismatch() {
        CreateExpenseParticipantDTO p1 = new CreateExpenseParticipantDTO("u1", 40.00);
        CreateExpenseParticipantDTO p2 = new CreateExpenseParticipantDTO("u2", 40.00);
        
        CreateExpenseParticipantDTO payer = new CreateExpenseParticipantDTO("u1", 100.00);

        ExpenseInputDTO dto = Instancio.of(ExpenseInputDTO.class)
            .set(org.instancio.Select.field(ExpenseInputDTO::amount), 100.00)
            .set(org.instancio.Select.field(ExpenseInputDTO::participants), List.of(p1, p2))
            .set(org.instancio.Select.field(ExpenseInputDTO::payers), List.of(payer))
            .create();

        InvalidDataException ex = assertThrows(InvalidDataException.class, () -> strategy.validate(dto));
        assertEquals(DEBTORS_AMOUNT_MISMATCH, ex.getMessage());
    }

    @Test
    void validate_fail_unequalDivision() {
        CreateExpenseParticipantDTO p1 = new CreateExpenseParticipantDTO("u1", 20.00);
        CreateExpenseParticipantDTO p2 = new CreateExpenseParticipantDTO("u2", 80.00);
        
        CreateExpenseParticipantDTO payer = new CreateExpenseParticipantDTO("u1", 100.00);

        ExpenseInputDTO dto = Instancio.of(ExpenseInputDTO.class)
            .set(org.instancio.Select.field(ExpenseInputDTO::amount), 100.00)
            .set(org.instancio.Select.field(ExpenseInputDTO::participants), List.of(p1, p2))
            .set(org.instancio.Select.field(ExpenseInputDTO::payers), List.of(payer))
            .create();

        InvalidDataException ex = assertThrows(InvalidDataException.class, () -> strategy.validate(dto));
        assertEquals(EQUAL_DIVISION_AMOUNT_MISMATCH, ex.getMessage());
    }
}