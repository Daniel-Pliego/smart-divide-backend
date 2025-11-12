package mr.limpios.smart_divide_backend.domain.strategies;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;
import mr.limpios.smart_divide_backend.infraestructure.dto.ExpenseInputDTO;

@Component
public class PercentageDivisionStrategy implements ExpenseDivisionStrategy {

    @Override
    public List<CalculatedBalance> calculate(ExpenseInputDTO dto) {
        BigDecimal totalAmount = BigDecimal.valueOf(dto.amount());
        BigDecimal totalPercentage = BigDecimal.ZERO;
        List<CalculatedBalance> shares = new ArrayList<>();

        for (var balance : dto.balances()) {
            BigDecimal percentage = BigDecimal.valueOf(balance.amountToPaid());
            totalPercentage = totalPercentage.add(percentage);

            // CÁLCULO: (Total * Porcentaje) / 100
            BigDecimal calculatedAmount = totalAmount
                    .multiply(percentage)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            shares.add(new CalculatedBalance(balance.debtorId(), calculatedAmount));
        }

        if (totalPercentage.compareTo(BigDecimal.valueOf(100)) != 0) {
            throw new InvalidDataException("Total percentage must be 100%. Current: " + totalPercentage);
        }

        return shares;
    }

}
