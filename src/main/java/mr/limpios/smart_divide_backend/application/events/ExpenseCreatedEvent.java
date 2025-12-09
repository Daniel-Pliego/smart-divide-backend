package mr.limpios.smart_divide_backend.application.events;

import mr.limpios.smart_divide_backend.domain.models.Expense;

public record ExpenseCreatedEvent(Expense expense) {}