package mr.limpios.smart_divide_backend.domain.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record Expense(
        String id,
        String type,
        String description,
        BigDecimal amount,
        String evidenceUrl,
        LocalDateTime createdAt,
        DivisionType divisionType,
        Group group,
        List<ExpenseParticipant> participants,
        List<ExpenseBalance> balances) {}
