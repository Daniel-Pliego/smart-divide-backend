package mr.limpios.smart_divide_backend.application.dtos.ExpenseDetails;

import java.math.BigDecimal;

public record ExpenseUserAmountDTO(ExpenseParticipantDTO participant, BigDecimal amount) {
}
