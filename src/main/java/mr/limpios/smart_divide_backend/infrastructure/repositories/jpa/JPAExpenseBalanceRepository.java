package mr.limpios.smart_divide_backend.infrastructure.repositories.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mr.limpios.smart_divide_backend.infrastructure.schemas.ExpenseBalanceSchema;

@Repository
public interface JPAExpenseBalanceRepository extends JpaRepository<ExpenseBalanceSchema, Integer> {
  List<ExpenseBalanceSchema> findByExpenseId(String expenseId);
}
