package mr.limpios.smart_divide_backend.aplication.repositories;

import java.util.List;
import java.util.Optional;

import mr.limpios.smart_divide_backend.domain.models.ExpenseGroupBalance;

public interface ExpenseGroupBalanceRepository {
    ExpenseGroupBalance saveExpenseGroupBalance(ExpenseGroupBalance expenseGroupBalance);
    Optional<ExpenseGroupBalance> findByCreditorAndDebtorAndGroup(String creditorId, String debtorId, String groupId);
    List<ExpenseGroupBalance> findByGroupIdAndCreditorId(String groupId, String creditorId);
    List<ExpenseGroupBalance> findByGroupIdAndDebtorId(String groupId, String debtorId);

    void deleteExpenseGroupBalance(Integer id);

}
