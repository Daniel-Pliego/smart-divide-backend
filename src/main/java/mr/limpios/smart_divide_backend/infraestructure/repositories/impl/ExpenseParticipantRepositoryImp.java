package mr.limpios.smart_divide_backend.infraestructure.repositories.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mr.limpios.smart_divide_backend.aplication.repositories.ExpenseParticipantRepository;
import mr.limpios.smart_divide_backend.infraestructure.repositories.jpa.JPAExpenseParticipantRepository;

@Repository
public class ExpenseParticipantRepositoryImp implements ExpenseParticipantRepository {
  @Autowired
  private JPAExpenseParticipantRepository jpaExpenseParticipantRepository;
}
