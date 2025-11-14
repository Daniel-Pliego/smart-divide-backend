package mr.limpios.smart_divide_backend.domain.strategies;

import java.util.List;

import mr.limpios.smart_divide_backend.infraestructure.dto.ExpenseInputDTO;

public interface ExpenseDivisionStrategy {
    List<CalculatedBalance> calculate(ExpenseInputDTO addExpenseDTO);
}
