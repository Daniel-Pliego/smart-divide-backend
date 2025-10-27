package mr.limpios.smart_divide_backend.domain.models;

import java.math.BigDecimal;

public record ExpenseBalance(Integer id, User creditor, User debtor, BigDecimal amountToPaid) {}
