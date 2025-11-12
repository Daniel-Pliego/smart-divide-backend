package mr.limpios.smart_divide_backend.domain.strategies;

import mr.limpios.smart_divide_backend.infraestructure.dto.AddExpenseDTO;

public interface ExpenseDivisionStrategy {
    void validate(AddExpenseDTO addExpenseDTO);
}
