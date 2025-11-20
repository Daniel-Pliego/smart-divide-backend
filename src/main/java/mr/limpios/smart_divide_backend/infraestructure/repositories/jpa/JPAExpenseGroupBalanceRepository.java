package mr.limpios.smart_divide_backend.infraestructure.repositories.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mr.limpios.smart_divide_backend.infraestructure.schemas.ExpenseGroupBalanceSchema;

@Repository
public interface JPAExpenseGroupBalanceRepository
        extends JpaRepository<ExpenseGroupBalanceSchema, Integer> {
    Optional<ExpenseGroupBalanceSchema> findByCreditor_IdAndDebtor_IdAndGroup_Id(
            String creditorId,
            String debtorId,
            String groupId);

    List<ExpenseGroupBalanceSchema> findByGroupIdAndCreditorId(String groupId, String creditorId);
    List<ExpenseGroupBalanceSchema> findByGroupIdAndDebtorId(String groupId, String debtorId);
}
