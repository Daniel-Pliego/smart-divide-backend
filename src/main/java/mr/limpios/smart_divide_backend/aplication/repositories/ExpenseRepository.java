package mr.limpios.smart_divide_backend.aplication.repositories;

import mr.limpios.smart_divide_backend.domain.models.Expense;

public interface ExpenseRepository {
    Expense saveExpense(Expense expense);
}
