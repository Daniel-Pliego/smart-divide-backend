package mr.limpios.smart_divide_backend.aplication.repositories;

import mr.limpios.smart_divide_backend.domain.models.Expense;

import java.util.List;

public interface ExpenseRepository {
    Expense saveExpense(Expense expense);
    List<Expense> findByGroupId(String groupId);
}
