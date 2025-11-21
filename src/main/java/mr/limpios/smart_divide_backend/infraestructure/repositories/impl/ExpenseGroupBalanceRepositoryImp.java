package mr.limpios.smart_divide_backend.infraestructure.repositories.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
  public ExpenseGroupBalance saveExpenseGroupBalance(ExpenseGroupBalance expenseGroupBalance) {
    ExpenseGroupBalanceSchema expenseGroupBalanceSchema = this.jpaExpenseGroupBalanceRepository
        .save(ExpenseGroupBalanceMapper.toSchema(expenseGroupBalance));

    return ExpenseGroupBalanceMapper.toModel(expenseGroupBalanceSchema);
  }

  @Override
  public Optional<ExpenseGroupBalance> findByCreditorAndDebtorAndGroup(String creditorId,
      String debtorId, String groupId) {
    return jpaExpenseGroupBalanceRepository
        .findByCreditor_IdAndDebtor_IdAndGroup_Id(creditorId, debtorId, groupId)
        .map(ExpenseGroupBalanceMapper::toModel);
  }

  @Override
  public List<ExpenseGroupBalance> findByGroupIdAndCreditorId(String groupId, String creditorId) {
    return jpaExpenseGroupBalanceRepository.findByGroupIdAndCreditorId(groupId, creditorId).stream()
        .map(ExpenseGroupBalanceMapper::toModel).collect(Collectors.toList());
  }

  @Override
  public List<ExpenseGroupBalance> findByGroupIdAndDebtorId(String groupId, String debtorId) {
    return jpaExpenseGroupBalanceRepository.findByGroupIdAndDebtorId(groupId, debtorId).stream()
        .map(ExpenseGroupBalanceMapper::toModel).collect(Collectors.toList());
  }

  @Override
  public List<ExpenseGroupBalance> findByGroupIdAndParticipant(String groupId, String participantId) {
    return jpaExpenseGroupBalanceRepository.findByGroupIdAndParticipantId(groupId, participantId).stream()
        .map(ExpenseGroupBalanceMapper::toModel).collect(Collectors.toList());
  }

  @Override
  public void deleteExpenseGroupBalance(Integer id) {
    jpaExpenseGroupBalanceRepository.deleteById(id);
  }

  @Override
  public BigDecimal getTotalDebtsByGroupAndDebtor(String groupId, String userId) {
    return jpaExpenseGroupBalanceRepository.sumDebtsByGroupAndDebtor(groupId, userId);
  }

  @Override
  public BigDecimal getTotalCreditsByGroupAndDebtor(String groupId, String userId) {
    return jpaExpenseGroupBalanceRepository.sumCreditsByGroupAndDebtor(groupId, userId);
  }

}
