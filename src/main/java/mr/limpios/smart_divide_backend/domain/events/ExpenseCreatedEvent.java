package mr.limpios.smart_divide_backend.domain.events;

import mr.limpios.smart_divide_backend.domain.models.Expense;

public record ExpenseCreatedEvent(Expense expense) {}