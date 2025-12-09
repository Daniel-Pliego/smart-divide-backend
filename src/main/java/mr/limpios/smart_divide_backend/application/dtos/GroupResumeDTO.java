package mr.limpios.smart_divide_backend.application.dtos;

import java.math.BigDecimal;

public record GroupResumeDTO(
        String id,
        String name,
        String type,        
        BigDecimal totalDebts,
        BigDecimal totalCredits) {}
