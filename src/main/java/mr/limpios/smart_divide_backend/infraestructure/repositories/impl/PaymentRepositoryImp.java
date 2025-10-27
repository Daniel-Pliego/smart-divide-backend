package mr.limpios.smart_divide_backend.infraestructure.repositories.impl;

import mr.limpios.smart_divide_backend.aplication.repositories.PaymentRepository;
import mr.limpios.smart_divide_backend.infraestructure.repositories.jpa.JPAPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentRepositoryImp implements PaymentRepository {
  @Autowired
  private JPAPaymentRepository jpaPaymentRepository;
}
