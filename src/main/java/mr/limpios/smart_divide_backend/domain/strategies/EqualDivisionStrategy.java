package mr.limpios.smart_divide_backend.domain.strategies;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;
import mr.limpios.smart_divide_backend.infraestructure.dto.ExpenseInputDTO;
import mr.limpios.smart_divide_backend.infraestructure.dto.ExpenseDebtorDTO;

@Component
public class EqualDivisionStrategy implements ExpenseDivisionStrategy {

    @Override
    public List<CalculatedBalance> calculate(ExpenseInputDTO addExpenseDTO) {
        double equalShare = addExpenseDTO.amount() / addExpenseDTO.balances().size();
        for (ExpenseDebtorDTO balance : addExpenseDTO.balances()) {
            if (Double.compare(balance.amountToPaid(), equalShare) != 0) {
                throw new InvalidDataException(
                        "For EQUAL division, each debtor must pay the same amount: " + equalShare);
            }
        }

        return addExpenseDTO.balances().stream()
            .map(b -> new CalculatedBalance(b.debtorId(), BigDecimal.valueOf(b.amountToPaid())))
            .collect(Collectors.toList());
    }
}
