package mr.limpios.smart_divide_backend.domain.strategies;

import org.springframework.stereotype.Component;

import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;
import mr.limpios.smart_divide_backend.infraestructure.dto.AddExpenseDTO;

@Component
public class CustomDivisionStrategy implements ExpenseDivisionStrategy {

    @Override
    public void validate(AddExpenseDTO addExpenseDTO) {
        double debtorsDTOTotalAmount = addExpenseDTO.balances().stream()
                .map(balance -> balance.amountToPaid())
                .reduce(0d, Double::sum);

        if (Double.compare(debtorsDTOTotalAmount, addExpenseDTO.amount()) != 0) {
            throw new InvalidDataException(
                    "The sum of debtors amounts does not equal the total expense amount");
        }
    }

}
