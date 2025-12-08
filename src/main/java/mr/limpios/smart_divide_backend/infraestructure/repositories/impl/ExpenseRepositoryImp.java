package mr.limpios.smart_divide_backend.infraestructure.repositories.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mr.limpios.smart_divide_backend.aplication.repositories.ExpenseRepository;
import mr.limpios.smart_divide_backend.domain.models.Expense;
import mr.limpios.smart_divide_backend.infraestructure.mappers.ExpenseMapper;
import mr.limpios.smart_divide_backend.infraestructure.repositories.jpa.JPAExpenseRepository;
import mr.limpios.smart_divide_backend.infraestructure.schemas.ExpenseSchema;

@Repository
public class ExpenseRepositoryImp implements ExpenseRepository {

  @Autowired
  private JPAExpenseRepository jpaExpenseRepository;

  @Override
  public Expense saveExpense(Expense expense) {
    ExpenseSchema expenseSchema = this.jpaExpenseRepository.save(ExpenseMapper.toSchema(expense));

    return ExpenseMapper.toModel(expenseSchema);
  }

  @Override
  public List<Expense> findByGroupId(String groupId) {
    return jpaExpenseRepository.findByGroupId(groupId).stream().map(ExpenseMapper::toModel)
        .collect(Collectors.toList());
  }

  @Override
  public Expense findById(String expenseId) {
    ExpenseSchema expenseSchema = jpaExpenseRepository.findById(expenseId).orElse(null);
    if (Objects.isNull(expenseSchema)) {
      return null;
    }
    return ExpenseMapper.toModel(expenseSchema);
  }

  @Override
  public void deleteById(String expenseId) {
    this.jpaExpenseRepository.deleteById(expenseId);
  }
}
