package mr.limpios.smart_divide_backend.domain.strategies;

import org.springframework.stereotype.Component;

import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;
import mr.limpios.smart_divide_backend.infraestructure.dto.AddExpenseDTO;
import mr.limpios.smart_divide_backend.infraestructure.dto.AddExpenseDebtorsDTO;

@Component
public class EqualDivisionStrategy implements ExpenseDivisionStrategy {

    @Override
    public void validate(AddExpenseDTO addExpenseDTO) {
        double equalShare = addExpenseDTO.amount() / addExpenseDTO.balances().size();
        for (AddExpenseDebtorsDTO balance : addExpenseDTO.balances()) {
            if (Double.compare(balance.amountToPaid(), equalShare) != 0) {
                throw new InvalidDataException(
                        "For EQUAL division, each debtor must pay the same amount: " + equalShare);
            }
        }
    }
}
