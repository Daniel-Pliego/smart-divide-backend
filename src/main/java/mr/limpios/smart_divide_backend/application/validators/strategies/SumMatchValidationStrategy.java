package mr.limpios.smart_divide_backend.application.validators.strategies;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.DEBTORS_AMOUNT_MISMATCH;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import mr.limpios.smart_divide_backend.application.dtos.ExpenseInputDTO;
import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;

@Component
public class SumMatchValidationStrategy extends AbstractExpenseValidationStrategy {

  @Override
  public void validate(ExpenseInputDTO dto) {
    validateParticipants(dto);
    super.validatePayers(dto);
  }

  private void validateParticipants(ExpenseInputDTO dto) {
    BigDecimal totalExpenseAmount = BigDecimal.valueOf(dto.amount());
    BigDecimal totalDivision = super.getSumOfAmount(dto.participants());

    if (totalDivision.compareTo(totalExpenseAmount) != 0) {
      throw new InvalidDataException(DEBTORS_AMOUNT_MISMATCH);
    }
  }
}
