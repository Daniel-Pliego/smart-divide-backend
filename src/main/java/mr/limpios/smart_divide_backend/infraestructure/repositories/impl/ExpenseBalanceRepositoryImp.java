package mr.limpios.smart_divide_backend.infraestructure.repositories.impl;

import mr.limpios.smart_divide_backend.domain.models.ExpenseBalance;
import mr.limpios.smart_divide_backend.infraestructure.mappers.ExpenseBalanceMapper;
import mr.limpios.smart_divide_backend.infraestructure.schemas.ExpenseBalanceSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mr.limpios.smart_divide_backend.aplication.repositories.ExpenseBalanceRepository;
import mr.limpios.smart_divide_backend.infraestructure.repositories.jpa.JPAExpenseBalanceRepository;

import java.util.List;
import java.util.stream.Collectors;

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
