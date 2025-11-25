package mr.limpios.smart_divide_backend.infraestructure.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mr.limpios.smart_divide_backend.infraestructure.schemas.ExpenseBalanceSchema;

import java.util.List;

@Repository
public interface JPAExpenseBalanceRepository extends JpaRepository<ExpenseBalanceSchema, Integer> {
    List<ExpenseBalanceSchema> findByExpenseId(String expenseId);
}
