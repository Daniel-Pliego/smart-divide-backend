package mr.limpios.smart_divide_backend.infraestructure.repositories.impl;

import mr.limpios.smart_divide_backend.aplication.repositories.ExpenseGroupBalance;
import mr.limpios.smart_divide_backend.infraestructure.repositories.jpa.JPAExpenseGroupBalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ExpenseGroupBalanceRepositoryImp implements ExpenseGroupBalance {
  @Autowired
  private JPAExpenseGroupBalanceRepository jpaExpenseGroupBalanceRepository;
}
