package mr.limpios.smart_divide_backend.infraestructure.repositories.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mr.limpios.smart_divide_backend.infraestructure.schemas.UserDeviceTokenSchema;

@Repository
public interface JPAUserDeviceTokenRepository extends JpaRepository<UserDeviceTokenSchema, String> {
  Optional<UserDeviceTokenSchema> findByUserId(String userId);

  List<UserDeviceTokenSchema> findByUserIdIn(List<String> userIds);
}
