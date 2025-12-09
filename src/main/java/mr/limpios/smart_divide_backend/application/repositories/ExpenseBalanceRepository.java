package mr.limpios.smart_divide_backend.application.repositories;

import java.util.List;

import mr.limpios.smart_divide_backend.domain.models.ExpenseBalance;

public interface ExpenseBalanceRepository {
  List<ExpenseBalance> findAllByExpenseId(String expenseId);
}
