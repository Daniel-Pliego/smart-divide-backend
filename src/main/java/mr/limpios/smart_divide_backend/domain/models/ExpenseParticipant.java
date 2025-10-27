package mr.limpios.smart_divide_backend.domain.models;

import java.math.BigDecimal;

public record ExpenseParticipant(
        Integer id, User payer, BigDecimal amountPaid, BigDecimal mustPaid) {}
