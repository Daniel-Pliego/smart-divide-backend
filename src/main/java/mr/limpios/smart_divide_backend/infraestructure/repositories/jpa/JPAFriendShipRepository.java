package mr.limpios.smart_divide_backend.infraestructure.repositories.jpa;

import mr.limpios.smart_divide_backend.infraestructure.schemas.FriendShipSchema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JPAFriendShipRepository extends JpaRepository<FriendShipSchema, Integer> {
}
