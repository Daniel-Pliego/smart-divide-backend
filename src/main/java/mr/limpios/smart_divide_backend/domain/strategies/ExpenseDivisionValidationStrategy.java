package mr.limpios.smart_divide_backend.domain.strategies;

import mr.limpios.smart_divide_backend.domain.dto.ExpenseInputDTO;

public interface ExpenseDivisionValidationStrategy {
    ParticipantsPayersOfExpenses validate(ExpenseInputDTO addExpenseDTO);
}
