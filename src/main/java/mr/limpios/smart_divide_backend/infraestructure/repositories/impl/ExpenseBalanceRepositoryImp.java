package mr.limpios.smart_divide_backend.infraestructure.repositories.impl;

import mr.limpios.smart_divide_backend.aplication.repositories.ExpenseBalanceRepository;
import mr.limpios.smart_divide_backend.infraestructure.repositories.jpa.JPAExpenseBalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ExpenseBalanceRepositoryImp implements ExpenseBalanceRepository {
  @Autowired
  private JPAExpenseBalanceRepository jpaExpenseBalanceRepository;
}
