package mr.limpios.smart_divide_backend.aplication.repositories;

import java.util.List;

import mr.limpios.smart_divide_backend.domain.models.Expense;

public interface ExpenseRepository {
  Expense saveExpense(Expense expense);

  List<Expense> findByGroupId(String groupId);
}
