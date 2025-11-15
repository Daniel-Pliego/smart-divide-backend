package mr.limpios.smart_divide_backend.infraestructure.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ExpenseDetailDTO(
        Integer id,
        String type,
        String description,
        BigDecimal amount,
        LocalDateTime createdAt,
        List<ExpensePayerDTO> payers
) {}
