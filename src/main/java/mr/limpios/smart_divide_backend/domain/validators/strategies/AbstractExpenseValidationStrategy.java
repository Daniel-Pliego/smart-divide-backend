package mr.limpios.smart_divide_backend.domain.validators.strategies;

import java.math.BigDecimal;
import java.util.List;

import mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants;
import mr.limpios.smart_divide_backend.domain.dto.ExpenseInputDTO;
import mr.limpios.smart_divide_backend.domain.dto.ExpenseParticipantDTO;
import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;

public abstract class AbstractExpenseValidationStrategy
    implements ExpenseDivisionValidationStrategy {
  protected void validatePayers(ExpenseInputDTO dto) {
    BigDecimal totalAmount = BigDecimal.valueOf(dto.amount());
    BigDecimal totalPayers = getSumOfAmount(dto.payers());

    if (totalPayers.compareTo(totalAmount) != 0) {
      throw new InvalidDataException(ExceptionsConstants.PAYERS_AMOUNT_MISMATCH);
    }
  }

  protected BigDecimal getSumOfAmount(List<ExpenseParticipantDTO> memberList) {
    return memberList.stream().map(p -> BigDecimal.valueOf(p.amount())).reduce(BigDecimal.ZERO,
        BigDecimal::add);
  }
}
