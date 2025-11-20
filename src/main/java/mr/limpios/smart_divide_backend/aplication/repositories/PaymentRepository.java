package mr.limpios.smart_divide_backend.aplication.repositories;

import java.util.List;

import mr.limpios.smart_divide_backend.domain.models.Payment;

public interface PaymentRepository {
  List<Payment> findByGroupId(String groupId);
}
