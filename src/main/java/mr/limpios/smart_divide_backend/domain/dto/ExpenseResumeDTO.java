package mr.limpios.smart_divide_backend.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExpenseResumeDTO(
        String id,
        String type,
        String description,
        BigDecimal amount,
        LocalDateTime createdAt) {
}
