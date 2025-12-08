package mr.limpios.smart_divide_backend.application.validators.strategies;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.PAYERS_AMOUNT_MISMATCH;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;

import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.Test;

import mr.limpios.smart_divide_backend.application.dtos.CreateExpenseParticipantDTO;
import mr.limpios.smart_divide_backend.application.dtos.ExpenseInputDTO;
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
        CreateExpenseParticipantDTO payer1 = new CreateExpenseParticipantDTO("u1", 60.00);
        CreateExpenseParticipantDTO payer2 = new CreateExpenseParticipantDTO("u2", 40.00);

        ExpenseInputDTO dto = Instancio.of(ExpenseInputDTO.class)
            .set(Select.field(ExpenseInputDTO::amount), 100.00)
            .set(Select.field(ExpenseInputDTO::payers), List.of(payer1, payer2))
            .create();

        assertDoesNotThrow(() -> strategy.validatePayers(dto));
    }

    @Test
    void validatePayers_fail_amountsMismatch() {
        CreateExpenseParticipantDTO payer1 = new CreateExpenseParticipantDTO("u1", 60.00);
        CreateExpenseParticipantDTO payer2 = new CreateExpenseParticipantDTO("u2", 30.00);

        ExpenseInputDTO dto = Instancio.of(ExpenseInputDTO.class)
            .set(Select.field(ExpenseInputDTO::amount), 100.00)
            .set(Select.field(ExpenseInputDTO::payers), List.of(payer1, payer2))
            .create();

        InvalidDataException ex = assertThrows(InvalidDataException.class, () -> strategy.validatePayers(dto));
        assertEquals(PAYERS_AMOUNT_MISMATCH, ex.getMessage());
    }

    @Test
    void getSumOfAmount_calculatesCorrectly() {
        CreateExpenseParticipantDTO p1 = new CreateExpenseParticipantDTO("u1", 10.50);
        CreateExpenseParticipantDTO p2 = new CreateExpenseParticipantDTO("u2", 20.25);
        List<CreateExpenseParticipantDTO> list = List.of(p1, p2);

        BigDecimal result = strategy.getSumOfAmount(list);

        assertEquals(new BigDecimal("30.75"), result);
    }
}