package mr.limpios.smart_divide_backend.domain.validators.strategies;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.PAYERS_AMOUNT_MISMATCH;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;

import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.Test;

import mr.limpios.smart_divide_backend.domain.dto.ExpenseInputDTO;
import mr.limpios.smart_divide_backend.domain.dto.ExpenseParticipantDTO;
import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;

class AbstractExpenseValidationStrategyTest {

    private static class TestableStrategy extends AbstractExpenseValidationStrategy {
        @Override
        public void validate(ExpenseInputDTO dto) {
        }
    }

    private final TestableStrategy strategy = new TestableStrategy();

    @Test
    void validatePayers_success_amountsMatch() {
        ExpenseParticipantDTO payer1 = new ExpenseParticipantDTO("u1", 60.00);
        ExpenseParticipantDTO payer2 = new ExpenseParticipantDTO("u2", 40.00);

        ExpenseInputDTO dto = Instancio.of(ExpenseInputDTO.class)
            .set(Select.field(ExpenseInputDTO::amount), 100.00)
            .set(Select.field(ExpenseInputDTO::payers), List.of(payer1, payer2))
            .create();

        assertDoesNotThrow(() -> strategy.validatePayers(dto));
    }

    @Test
    void validatePayers_fail_amountsMismatch() {
        ExpenseParticipantDTO payer1 = new ExpenseParticipantDTO("u1", 60.00);
        ExpenseParticipantDTO payer2 = new ExpenseParticipantDTO("u2", 30.00);

        ExpenseInputDTO dto = Instancio.of(ExpenseInputDTO.class)
            .set(Select.field(ExpenseInputDTO::amount), 100.00)
            .set(Select.field(ExpenseInputDTO::payers), List.of(payer1, payer2))
            .create();

        InvalidDataException ex = assertThrows(InvalidDataException.class, () -> strategy.validatePayers(dto));
        assertEquals(PAYERS_AMOUNT_MISMATCH, ex.getMessage());
    }

    @Test
    void getSumOfAmount_calculatesCorrectly() {
        ExpenseParticipantDTO p1 = new ExpenseParticipantDTO("u1", 10.50);
        ExpenseParticipantDTO p2 = new ExpenseParticipantDTO("u2", 20.25);
        List<ExpenseParticipantDTO> list = List.of(p1, p2);

        BigDecimal result = strategy.getSumOfAmount(list);

        assertEquals(new BigDecimal("30.75"), result);
    }
}