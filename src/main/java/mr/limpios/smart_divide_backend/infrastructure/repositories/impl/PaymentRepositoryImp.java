package mr.limpios.smart_divide_backend.infrastructure.repositories.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mr.limpios.smart_divide_backend.application.repositories.PaymentRepository;
import mr.limpios.smart_divide_backend.domain.models.Payment;
import mr.limpios.smart_divide_backend.infrastructure.mappers.PaymentMapper;
import mr.limpios.smart_divide_backend.infrastructure.repositories.jpa.JPAPaymentRepository;
import mr.limpios.smart_divide_backend.infrastructure.schemas.PaymentSchema;

@Repository
public class PaymentRepositoryImp implements PaymentRepository {
  @Autowired
  private JPAPaymentRepository jpaPaymentRepository;

  @Override
  public List<Payment> findByGroupId(String groupId) {
    return jpaPaymentRepository.findByGroupId(groupId).stream().map(PaymentMapper::toModel)
        .collect(Collectors.toList());
  }

  @Override
  public Payment savePayment(Payment payment) {
    PaymentSchema paymentSchema = this.jpaPaymentRepository.save(PaymentMapper.toSchema(payment));
    return PaymentMapper.toModel(paymentSchema);
  }
}
