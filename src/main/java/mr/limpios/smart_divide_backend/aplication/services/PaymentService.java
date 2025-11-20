package mr.limpios.smart_divide_backend.aplication.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import mr.limpios.smart_divide_backend.aplication.repositories.PaymentRepository;
import mr.limpios.smart_divide_backend.domain.dto.PaymentDetailDTO;
import mr.limpios.smart_divide_backend.domain.dto.PaymentUserDTO;
import mr.limpios.smart_divide_backend.domain.models.Payment;

@Service
public class PaymentService {

  private final PaymentRepository paymentRepository;

  public PaymentService(PaymentRepository paymentRepository) {
    this.paymentRepository = paymentRepository;
  }

  public List<PaymentDetailDTO> getPaymentsByGroup(String groupId) {
    List<Payment> payments = paymentRepository.findByGroupId(groupId);

    return payments.stream().map(this::buildPaymentDetailDTO).collect(Collectors.toList());
  }

  private PaymentDetailDTO buildPaymentDetailDTO(Payment payment) {

    PaymentUserDTO fromUser =
        new PaymentUserDTO(payment.fromUser().name(), payment.fromUser().lastName());

    PaymentUserDTO toUser =
        new PaymentUserDTO(payment.toUser().name(), payment.toUser().lastName());

    return new PaymentDetailDTO(payment.id(), fromUser, toUser, payment.amount(),
        payment.createdAt());
  }
}
