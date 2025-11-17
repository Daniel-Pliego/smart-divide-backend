package mr.limpios.smart_divide_backend.domain.strategies;

import java.math.BigDecimal;

public record ValidatedMembers(String memberId, BigDecimal amount) {}