package mr.limpios.smart_divide_backend.application.validators.strategies;

import mr.limpios.smart_divide_backend.application.dtos.ExpenseInputDTO;

public interface ExpenseDivisionValidationStrategy {
  void validate(ExpenseInputDTO addExpenseDTO);
}
