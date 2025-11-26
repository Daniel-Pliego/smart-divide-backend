package mr.limpios.smart_divide_backend.domain.dto.ExpenseDetails;

import java.math.BigDecimal;

public record ExpensePayerDetailDTO(ExpenseParticipantDTO participant, BigDecimal amountPaid, BigDecimal amountBorrowed) {
}