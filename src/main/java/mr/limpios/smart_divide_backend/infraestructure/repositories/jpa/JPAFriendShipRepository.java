package mr.limpios.smart_divide_backend.infraestructure.repositories.jpa;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import mr.limpios.smart_divide_backend.infraestructure.schemas.FriendshipSchema;

import java.util.Optional;

@Repository
public interface JPAFriendShipRepository extends JpaRepository<FriendshipSchema, Integer> {
  boolean existsByRequesterIdAndFriendIdOrRequesterIdAndFriendId(String requesterId1,
      String friendId1, String requesterId2, String friendId2);

  Set<FriendshipSchema> findByRequesterIdOrFriendId(String requesterId, String friendId);

  @Query("SELECT fs FROM friendship fs WHERE " +
           "((fs.requester.id = :userA AND fs.friend.id = :userB) OR " +
           "(fs.requester.id = :userB AND fs.friend.id = :userA)) AND " +
           "fs.confirmed = true")
  Optional<FriendshipSchema> findConfirmedFriendship(String userA, String userB);
}
