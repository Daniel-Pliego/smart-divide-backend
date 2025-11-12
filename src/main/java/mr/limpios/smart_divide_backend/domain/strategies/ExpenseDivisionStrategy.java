package mr.limpios.smart_divide_backend.domain.strategies;

import java.util.List;

import mr.limpios.smart_divide_backend.infraestructure.dto.AddExpenseDTO;

public interface ExpenseDivisionStrategy {
    List<CalculatedBalance> calculate(AddExpenseDTO addExpenseDTO);
}
