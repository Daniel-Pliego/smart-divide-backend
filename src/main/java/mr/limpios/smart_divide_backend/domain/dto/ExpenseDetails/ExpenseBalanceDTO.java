package mr.limpios.smart_divide_backend.domain.dto.ExpenseDetails;

import java.util.List;

public record ExpenseBalanceDTO(
        ExpenseParticipantDTO payer,
        List<DebtorBalanceDTO> debtors) {

}
