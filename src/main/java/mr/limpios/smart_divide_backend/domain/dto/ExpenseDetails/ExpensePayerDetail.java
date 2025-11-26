package mr.limpios.smart_divide_backend.domain.dto.ExpenseDetails;

import java.math.BigDecimal;

public record ExpensePayerDetail(ExpenseParticipantDTO participant, BigDecimal amountPaid) {
}