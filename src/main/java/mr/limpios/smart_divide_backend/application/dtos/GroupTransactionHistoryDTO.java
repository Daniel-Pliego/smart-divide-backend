package mr.limpios.smart_divide_backend.application.dtos;

import java.util.List;

public record GroupTransactionHistoryDTO(
        String id,
        String name,
        String description,
        String ownerId,
        String type,
        List<UserBalanceDTO> userBalance,
        List<PaymentDetailDTO> payments,
        List<ExpenseSummaryDTO> expenses
) {
}
