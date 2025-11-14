package mr.limpios.smart_divide_backend.domain.strategies;

import java.util.Map;

import org.springframework.stereotype.Component;

import mr.limpios.smart_divide_backend.domain.models.DivisionType;

@Component
public class ExpenseStrategyFactory {
    private final Map<DivisionType, ExpenseDivisionStrategy> strategies;

    public ExpenseStrategyFactory(Map<String, ExpenseDivisionStrategy> strategyMap) {
        this.strategies = Map.of(
            DivisionType.EQUAL, new EqualDivisionStrategy(),
            DivisionType.CUSTOM, new CustomDivisionStrategy(),
            DivisionType.PERCENTAGE, new PercentageDivisionStrategy()
        );
    }

    public ExpenseDivisionStrategy getStrategy(DivisionType type) {
        return strategies.get(type);
    }
}
