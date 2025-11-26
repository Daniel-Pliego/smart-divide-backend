package mr.limpios.smart_divide_backend.domain.validators.strategies;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.DEBTORS_AMOUNT_MISMATCH;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.PAYERS_AMOUNT_MISMATCH;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.Test;

import mr.limpios.smart_divide_backend.domain.dto.ExpenseInputDTO;
import mr.limpios.smart_divide_backend.domain.dto.ExpenseParticipantDTO;
import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;

class SumMatchValidationStrategyTest {

    private final SumMatchValidationStrategy strategy = new SumMatchValidationStrategy();

    @Test
    void validate_success_sumsMatch() {
        ExpenseParticipantDTO part1 = new ExpenseParticipantDTO("u1", 40.00);
        ExpenseParticipantDTO part2 = new ExpenseParticipantDTO("u2", 60.00);
        
        ExpenseParticipantDTO payer1 = new ExpenseParticipantDTO("u1", 100.00);

        ExpenseInputDTO dto = Instancio.of(ExpenseInputDTO.class)
            .set(Select.field(ExpenseInputDTO::amount), 100.00)
            .set(Select.field(ExpenseInputDTO::participants), List.of(part1, part2))
            .set(Select.field(ExpenseInputDTO::payers), List.of(payer1))
            .create();

        assertDoesNotThrow(() -> strategy.validate(dto));
    }

    @Test
    void validate_fail_participantsSumMismatch() {
        ExpenseParticipantDTO part1 = new ExpenseParticipantDTO("u1", 40.00);
        ExpenseParticipantDTO part2 = new ExpenseParticipantDTO("u2", 40.00); 
        
        ExpenseParticipantDTO payer1 = new ExpenseParticipantDTO("u1", 100.00);

        ExpenseInputDTO dto = Instancio.of(ExpenseInputDTO.class)
            .set(Select.field(ExpenseInputDTO::amount), 100.00)
            .set(Select.field(ExpenseInputDTO::participants), List.of(part1, part2))
            .set(Select.field(ExpenseInputDTO::payers), List.of(payer1))
            .create();

        InvalidDataException ex = assertThrows(InvalidDataException.class, () -> strategy.validate(dto));
        assertEquals(DEBTORS_AMOUNT_MISMATCH, ex.getMessage());
    }

    @Test
    void validate_fail_payersSumMismatch() {
        ExpenseParticipantDTO part1 = new ExpenseParticipantDTO("u1", 50.00);
        ExpenseParticipantDTO part2 = new ExpenseParticipantDTO("u2", 50.00);
        
        ExpenseParticipantDTO payer1 = new ExpenseParticipantDTO("u1", 90.00); 

        ExpenseInputDTO dto = Instancio.of(ExpenseInputDTO.class)
            .set(Select.field(ExpenseInputDTO::amount), 100.00)
            .set(Select.field(ExpenseInputDTO::participants), List.of(part1, part2))
            .set(Select.field(ExpenseInputDTO::payers), List.of(payer1))
            .create();

        InvalidDataException ex = assertThrows(InvalidDataException.class, () -> strategy.validate(dto));
        assertEquals(PAYERS_AMOUNT_MISMATCH, ex.getMessage());
    }
}