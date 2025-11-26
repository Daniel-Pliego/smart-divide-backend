package mr.limpios.smart_divide_backend.domain.validators.strategies;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mr.limpios.smart_divide_backend.domain.models.DivisionType;

class ExpenseValidationStrategyFactoryTest {

    private ExpenseValidationStrategyFactory factory;

    @BeforeEach
    void setUp() {
        factory = new ExpenseValidationStrategyFactory(Collections.emptyMap());
    }

    @Test
    void getStrategy_equal_returnsEqualStrategy() {
        ExpenseDivisionValidationStrategy strategy = factory.getStrategy(DivisionType.EQUAL);

        assertNotNull(strategy);
        assertInstanceOf(EqualDivisionValidationStrategy.class, strategy);
    }

    @Test
    void getStrategy_custom_returnsSumMatchStrategy() {
        ExpenseDivisionValidationStrategy strategy = factory.getStrategy(DivisionType.CUSTOM);

        assertNotNull(strategy);
        assertInstanceOf(SumMatchValidationStrategy.class, strategy);
    }

    @Test
    void getStrategy_percentage_returnsSumMatchStrategy() {
        ExpenseDivisionValidationStrategy strategy = factory.getStrategy(DivisionType.PERCENTAGE);

        assertNotNull(strategy);
        assertInstanceOf(SumMatchValidationStrategy.class, strategy);
    }
}