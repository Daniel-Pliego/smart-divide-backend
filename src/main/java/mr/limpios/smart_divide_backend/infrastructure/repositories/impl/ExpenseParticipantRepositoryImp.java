package mr.limpios.smart_divide_backend.infrastructure.repositories.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mr.limpios.smart_divide_backend.application.repositories.ExpenseParticipantRepository;
import mr.limpios.smart_divide_backend.infrastructure.repositories.jpa.JPAExpenseParticipantRepository;

@Repository
public class ExpenseParticipantRepositoryImp implements ExpenseParticipantRepository {
  @Autowired
  private JPAExpenseParticipantRepository jpaExpenseParticipantRepository;
}
