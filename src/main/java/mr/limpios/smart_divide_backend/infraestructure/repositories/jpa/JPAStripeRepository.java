package mr.limpios.smart_divide_backend.infraestructure.repositories.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mr.limpios.smart_divide_backend.infraestructure.schemas.StripeUserSchema;

@Repository
public interface JPAStripeRepository extends JpaRepository<StripeUserSchema, String> {
  Optional<StripeUserSchema> findByUserId(String userId);

  Optional<StripeUserSchema> findByStripeAccountId(String stripeAccountId);
}
