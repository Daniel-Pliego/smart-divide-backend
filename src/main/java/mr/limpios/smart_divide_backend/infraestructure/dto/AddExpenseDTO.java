package mr.limpios.smart_divide_backend.infraestructure.dto;

import java.math.BigDecimal;
import java.util.List;

import mr.limpios.smart_divide_backend.domain.models.DivisionType;
public record AddExpenseDTO(
        String type,
        String description,
        BigDecimal amount,
        String evidenUrl,
        DivisionType divisionType,
        List<AddExpenseDebtorsDTO> balances
) {}
