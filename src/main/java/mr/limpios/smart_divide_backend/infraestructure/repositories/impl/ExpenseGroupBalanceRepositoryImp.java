package mr.limpios.smart_divide_backend.infraestructure.repositories.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mr.limpios.smart_divide_backend.aplication.repositories.ExpenseGroupBalanceRepository;
import mr.limpios.smart_divide_backend.domain.models.ExpenseGroupBalance;
import mr.limpios.smart_divide_backend.infraestructure.mappers.ExpenseGroupBalanceMapper;
import mr.limpios.smart_divide_backend.infraestructure.repositories.jpa.JPAExpenseGroupBalanceRepository;
import mr.limpios.smart_divide_backend.infraestructure.schemas.ExpenseGroupBalanceSchema;

@Repository
public class ExpenseGroupBalanceRepositoryImp implements ExpenseGroupBalanceRepository {
  @Autowired
  private JPAExpenseGroupBalanceRepository jpaExpenseGroupBalanceRepository;

  @Override
  public ExpenseGroupBalance saveExpenseGroupBalance(
      ExpenseGroupBalance expenseGroupBalance) {
    ExpenseGroupBalanceSchema expenseGroupBalanceSchema = this.jpaExpenseGroupBalanceRepository
        .save(ExpenseGroupBalanceMapper.toSchema(expenseGroupBalance));

    return ExpenseGroupBalanceMapper.toModel(expenseGroupBalanceSchema);
  }

  @Override
  public Optional<ExpenseGroupBalance> findByCreditorAndDebtorAndGroup(String creditorId, String debtorId,
      String groupId) {
    return jpaExpenseGroupBalanceRepository.findByCreditor_IdAndDebtor_IdAndGroup_Id(creditorId, debtorId, groupId)
        .map(ExpenseGroupBalanceMapper::toModel);
  }

    @Override
    public void deleteExpenseGroupBalance(Integer id) {
        jpaExpenseGroupBalanceRepository.deleteById(id);
    }

}
