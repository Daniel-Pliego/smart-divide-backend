package mr.limpios.smart_divide_backend.aplication.repositories;

import mr.limpios.smart_divide_backend.domain.models.ExpenseBalance;

import java.util.List;

public interface ExpenseBalanceRepository {
    List<ExpenseBalance> findAllByExpenseId(String expenseId);
}
