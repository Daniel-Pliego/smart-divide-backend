package mr.limpios.smart_divide_backend.infraestructure.repositories.jpa;

import mr.limpios.smart_divide_backend.infraestructure.schemas.PaymentSchema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JPAPaymentRepository extends JpaRepository<PaymentSchema, String> {
    List<PaymentSchema> findByGroupId(String groupId);
}
