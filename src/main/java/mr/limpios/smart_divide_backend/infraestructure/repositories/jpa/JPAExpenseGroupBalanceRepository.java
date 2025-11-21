package mr.limpios.smart_divide_backend.infraestructure.repositories.jpa;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import mr.limpios.smart_divide_backend.infraestructure.schemas.ExpenseGroupBalanceSchema;

@Repository
public interface JPAExpenseGroupBalanceRepository
    extends JpaRepository<ExpenseGroupBalanceSchema, Integer> {
  Optional<ExpenseGroupBalanceSchema> findByCreditor_IdAndDebtor_IdAndGroup_Id(String creditorId,
      String debtorId, String groupId);

  List<ExpenseGroupBalanceSchema> findByGroupIdAndCreditorId(String groupId, String creditorId);

  List<ExpenseGroupBalanceSchema> findByGroupIdAndDebtorId(String groupId, String debtorId);


  @Query("""
      SELECT COALESCE(SUM(e.amount), 0)
      FROM expense_group_balance e
      WHERE e.group.id = :groupId
        AND e.debtor.id = :userId
      """)
  BigDecimal sumDebtsByGroupAndDebtor(String groupId, String userId);

  @Query("""
      SELECT COALESCE(SUM(e.amount), 0)
      FROM expense_group_balance e
      WHERE e.group.id = :groupId
        AND e.creditor.id = :userId
      """)
  BigDecimal sumCreditsByGroupAndDebtor(String groupId, String userId);


}
