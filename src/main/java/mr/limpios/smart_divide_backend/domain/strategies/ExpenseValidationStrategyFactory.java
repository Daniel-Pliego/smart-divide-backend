package mr.limpios.smart_divide_backend.domain.strategies;

import java.util.Map;

import org.springframework.stereotype.Component;

import mr.limpios.smart_divide_backend.domain.models.DivisionType;

@Component
public class ExpenseValidationStrategyFactory {
    private final Map<DivisionType, ExpenseDivisionValidationStrategy> strategies;

    public ExpenseValidationStrategyFactory(Map<String, ExpenseDivisionValidationStrategy> strategyMap) {
        this.strategies = Map.of(
            DivisionType.EQUAL, new EqualDivisionValidationStrategy(),
            DivisionType.CUSTOM, new SumMatchValidationStrategy(),
            DivisionType.PERCENTAGE, new SumMatchValidationStrategy()
        );
    }

    public ExpenseDivisionValidationStrategy getStrategy(DivisionType type) {
        return strategies.get(type);
    }
}
