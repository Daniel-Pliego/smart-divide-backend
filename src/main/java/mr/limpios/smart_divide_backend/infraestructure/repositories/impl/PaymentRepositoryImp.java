package mr.limpios.smart_divide_backend.infraestructure.repositories.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mr.limpios.smart_divide_backend.aplication.repositories.PaymentRepository;
import mr.limpios.smart_divide_backend.domain.models.Payment;
import mr.limpios.smart_divide_backend.infraestructure.mappers.PaymentMapper;
import mr.limpios.smart_divide_backend.infraestructure.repositories.jpa.JPAPaymentRepository;

@Repository
public class PaymentRepositoryImp implements PaymentRepository {
  @Autowired
  private JPAPaymentRepository jpaPaymentRepository;

  @Override
  public List<Payment> findByGroupId(String groupId) {
    return jpaPaymentRepository.findByGroupId(groupId).stream().map(PaymentMapper::toModel)
        .collect(Collectors.toList());
  }
}
