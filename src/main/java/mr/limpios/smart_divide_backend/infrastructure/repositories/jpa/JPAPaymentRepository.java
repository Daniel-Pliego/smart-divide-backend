package mr.limpios.smart_divide_backend.infrastructure.repositories.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mr.limpios.smart_divide_backend.infrastructure.schemas.PaymentSchema;

@Repository
public interface JPAPaymentRepository extends JpaRepository<PaymentSchema, String> {
  List<PaymentSchema> findByGroupId(String groupId);
}
