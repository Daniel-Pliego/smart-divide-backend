package mr.limpios.smart_divide_backend.domain.dto;

import java.math.BigDecimal;

import mr.limpios.smart_divide_backend.domain.dto.ExpenseDetails.ExpenseParticipantDTO;

public record BalanceDetailDTO(
                ExpenseParticipantDTO creditor,
                ExpenseParticipantDTO debtor,
                BigDecimal amount) {

}
