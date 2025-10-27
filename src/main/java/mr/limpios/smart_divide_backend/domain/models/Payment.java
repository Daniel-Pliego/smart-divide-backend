package mr.limpios.smart_divide_backend.domain.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Payment(
        String id,
        User fromUser,
        User toUser,
        BigDecimal amount,
        Group group,
        LocalDateTime createdAt) {}
