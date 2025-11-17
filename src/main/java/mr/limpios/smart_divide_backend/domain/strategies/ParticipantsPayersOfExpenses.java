package mr.limpios.smart_divide_backend.domain.strategies;


import mr.limpios.smart_divide_backend.domain.dto.ExpenseDebtorDTO;

import java.util.List;

public record ParticipantsPayersOfExpenses(List<ValidatedMembers> participants, List<ValidatedMembers> payers) {}