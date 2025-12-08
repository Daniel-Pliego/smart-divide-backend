package mr.limpios.smart_divide_backend.infrastructure.repositories.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mr.limpios.smart_divide_backend.application.repositories.ExpenseBalanceRepository;
import mr.limpios.smart_divide_backend.domain.models.ExpenseBalance;
import mr.limpios.smart_divide_backend.infrastructure.mappers.ExpenseBalanceMapper;
import mr.limpios.smart_divide_backend.infrastructure.repositories.jpa.JPAExpenseBalanceRepository;
import mr.limpios.smart_divide_backend.infrastructure.schemas.ExpenseBalanceSchema;

@Repository
public class ExpenseBalanceRepositoryImp implements ExpenseBalanceRepository {
  @Autowired
  private JPAExpenseBalanceRepository jpaExpenseBalanceRepository;

  @Override
  public List<ExpenseBalance> findAllByExpenseId(String expenseId) {
    List<ExpenseBalanceSchema> schemas = jpaExpenseBalanceRepository.findByExpenseId(expenseId);
    return schemas.stream().map(ExpenseBalanceMapper::toModel).collect(Collectors.toList());
  }
}
