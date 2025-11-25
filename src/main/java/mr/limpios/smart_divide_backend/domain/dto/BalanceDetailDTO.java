package mr.limpios.smart_divide_backend.domain.dto;

import java.math.BigDecimal;

public record BalanceDetailDTO(
        String creditorId,
        String debtorId,
        BigDecimal amount) {

}
