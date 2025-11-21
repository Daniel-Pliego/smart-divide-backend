package mr.limpios.smart_divide_backend.domain.validators.strategies;

import org.springframework.stereotype.Component;

import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;
import mr.limpios.smart_divide_backend.domain.dto.ExpenseInputDTO;
import mr.limpios.smart_divide_backend.domain.dto.ExpenseDebtorDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.*;

@Component
public class EqualDivisionValidationStrategy extends AbstractExpenseValidationStrategy {

    @Override
    public void validate(ExpenseInputDTO dto) {
        validateParticipants(dto);
        super.validatePayers(dto);
    }

    private void validateParticipants(ExpenseInputDTO dto) {
        BigDecimal totalAmount = BigDecimal.valueOf(dto.amount());
        BigDecimal totalDebt = super.getSumOfAmount(dto.participants());
        if (totalDebt.compareTo(totalAmount) != 0) {
            throw new InvalidDataException(
                    DEBTORS_AMOUNT_MISMATCH);
        }

        BigDecimal count = BigDecimal.valueOf(dto.participants().size());
        BigDecimal expectedShare = totalAmount.divide(count, 2, RoundingMode.HALF_UP);
        BigDecimal tolerance = new BigDecimal("0.01");

        for (ExpenseDebtorDTO participant : dto.participants()) {
            BigDecimal actualShare = BigDecimal.valueOf(participant.amount());
            BigDecimal difference = actualShare.subtract(expectedShare).abs();

            if (difference.compareTo(tolerance) > 0) {
                throw new InvalidDataException(
                        EQUAL_DIVISION_AMOUNT_MISMATCH);}
        }
    }
}
