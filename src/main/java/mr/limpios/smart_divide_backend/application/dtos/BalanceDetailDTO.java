package mr.limpios.smart_divide_backend.application.dtos;

import java.math.BigDecimal;

import mr.limpios.smart_divide_backend.application.dtos.ExpenseDetails.ExpenseParticipantDTO;

public record BalanceDetailDTO(
                ExpenseParticipantDTO creditor,
                ExpenseParticipantDTO debtor,
                BigDecimal amount) {

}
