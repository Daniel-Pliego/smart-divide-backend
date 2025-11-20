package mr.limpios.smart_divide_backend.domain.validators.strategies;

import mr.limpios.smart_divide_backend.domain.dto.ExpenseInputDTO;

public interface ExpenseDivisionValidationStrategy {
    void validate(ExpenseInputDTO addExpenseDTO);
}
