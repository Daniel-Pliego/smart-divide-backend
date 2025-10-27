package mr.limpios.smart_divide_backend.infraestructure.repositories.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import mr.limpios.smart_divide_backend.aplication.repositories.CardRepository;
import mr.limpios.smart_divide_backend.infraestructure.repositories.jpa.JPACardRepository;

@Repository
public class CardRepositoryImp implements CardRepository {
  @Autowired
  private JPACardRepository jpaCardRepository;
}
