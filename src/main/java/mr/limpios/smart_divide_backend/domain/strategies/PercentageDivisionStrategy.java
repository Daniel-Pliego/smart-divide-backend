package mr.limpios.smart_divide_backend.domain.strategies;

import org.springframework.stereotype.Component;

import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;
import mr.limpios.smart_divide_backend.infraestructure.dto.AddExpenseDTO;

@Component
public class PercentageDivisionStrategy implements ExpenseDivisionStrategy {

    @Override
    public void validate(AddExpenseDTO addExpenseDTO) {
        double totalPercentage = addExpenseDTO.balances().stream()
                .map(balance -> balance.amountToPaid())
                .reduce(0d, Double::sum);
        if (Double.compare(totalPercentage, 100.0) != 0) {
            throw new InvalidDataException("For PERCENTAGE division, the total percentage must equal 100%");
        }
    }

}
