package mr.limpios.smart_divide_backend.application.repositories;

import java.util.List;

import mr.limpios.smart_divide_backend.domain.models.Expense;

public interface ExpenseRepository {
  Expense saveExpense(Expense expense);

  List<Expense> findByGroupId(String groupId);

  Expense findById(String expenseId);

  void deleteById(String expenseId);
}
