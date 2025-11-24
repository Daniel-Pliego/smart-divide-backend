package mr.limpios.smart_divide_backend.domain.dto;

import java.util.List;

import mr.limpios.smart_divide_backend.domain.models.DivisionType;
public record ExpenseInputDTO(
        String type,
        String description,
        double amount,
        String evidenceUrl,
        DivisionType divisionType,
        List<ExpenseParticipantDTO> payers,
        List<ExpenseParticipantDTO> participants
) {}
