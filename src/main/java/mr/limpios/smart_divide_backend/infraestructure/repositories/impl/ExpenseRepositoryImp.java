package mr.limpios.smart_divide_backend.infraestructure.repositories.impl;

import mr.limpios.smart_divide_backend.aplication.repositories.Expense;
import mr.limpios.smart_divide_backend.infraestructure.repositories.jpa.JPAExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ExpenseRepositoryImp implements Expense {
  @Autowired
  private JPAExpenseRepository jpaExpenseRepository;
}
