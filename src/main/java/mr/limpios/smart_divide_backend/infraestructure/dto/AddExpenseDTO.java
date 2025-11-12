package mr.limpios.smart_divide_backend.infraestructure.dto;

import java.util.List;

import mr.limpios.smart_divide_backend.domain.models.DivisionType;
public record AddExpenseDTO(
        String type,
        String description,
        double amount,
        String evidenUrl,
        DivisionType divisionType,
        List<AddExpenseDebtorsDTO> balances
) {}
