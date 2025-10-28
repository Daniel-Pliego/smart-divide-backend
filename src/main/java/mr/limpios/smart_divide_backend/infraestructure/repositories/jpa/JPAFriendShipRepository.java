package mr.limpios.smart_divide_backend.infraestructure.repositories.jpa;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mr.limpios.smart_divide_backend.infraestructure.schemas.FriendShipSchema;

@Repository
public interface JPAFriendShipRepository extends JpaRepository<FriendShipSchema, Integer> {
  boolean existsByRequesterIdAndFriendIdOrRequesterIdAndFriendId(String requesterId1,
      String friendId1, String requesterId2, String friendId2);

  Set<FriendShipSchema> findByRequesterIdOrFriendId(String requesterId, String friendId);
}
