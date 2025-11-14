package mr.limpios.smart_divide_backend.domain.strategies;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.DEBTORS_AMOUNT_MISMATCH;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;
import mr.limpios.smart_divide_backend.infraestructure.dto.ExpenseInputDTO;

@Component
public class CustomDivisionStrategy implements ExpenseDivisionStrategy {

    @Override
    public List<CalculatedBalance> calculate(ExpenseInputDTO addExpenseDTO) {
        double debtorsDTOTotalAmount = addExpenseDTO.balances().stream()
                .map(balance -> balance.amountToPaid())
                .reduce(0d, Double::sum);

        if (Double.compare(debtorsDTOTotalAmount, addExpenseDTO.amount()) != 0) {
            throw new InvalidDataException(
                    DEBTORS_AMOUNT_MISMATCH);
        }

        return addExpenseDTO.balances().stream()
                .map(b -> new CalculatedBalance(b.debtorId(), BigDecimal.valueOf(b.amountToPaid())))
                .collect(Collectors.toList());
    }

}
