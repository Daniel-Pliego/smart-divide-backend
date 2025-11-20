package mr.limpios.smart_divide_backend.infraestructure.repositories.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.AllArgsConstructor;
import mr.limpios.smart_divide_backend.aplication.repositories.ExpenseRepository;
import mr.limpios.smart_divide_backend.domain.models.Expense;
import mr.limpios.smart_divide_backend.infraestructure.mappers.ExpenseMapper;
import mr.limpios.smart_divide_backend.infraestructure.repositories.jpa.JPAExpenseRepository;
import mr.limpios.smart_divide_backend.infraestructure.schemas.ExpenseSchema;

@Repository
@AllArgsConstructor
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
}
