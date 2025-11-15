package mr.limpios.smart_divide_backend.aplication.repositories;

import mr.limpios.smart_divide_backend.domain.models.Payment;

import java.util.List;

public interface PaymentRepository {
    List<Payment> findByGroupId(String groupId);
}
