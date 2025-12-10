package mr.limpios.smart_divide_backend.infrastructure.repositories.impl;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mr.limpios.smart_divide_backend.application.repositories.FriendshipRepository;
import mr.limpios.smart_divide_backend.domain.models.Friendship;
import mr.limpios.smart_divide_backend.infrastructure.mappers.FriendshipMapper;
import mr.limpios.smart_divide_backend.infrastructure.repositories.jpa.JPAFriendShipRepository;
import mr.limpios.smart_divide_backend.infrastructure.schemas.FriendshipSchema;

@Repository
public class FriendShipRepositoryImp implements FriendshipRepository {
  @Autowired
  private JPAFriendShipRepository jpaFriendShipRepository;

  @Override
  public void createFriendRequest(Friendship friendship) {
    jpaFriendShipRepository.save(FriendshipMapper.toSchema(friendship));
  }

  @Override
  public Set<Friendship> getAllFriendshipsByUserId(String userId) {
    Set<FriendshipSchema> friendshipsSchema =
        jpaFriendShipRepository.findByRequesterIdOrFriendId(userId, userId);

    return friendshipsSchema.stream().map(FriendshipMapper::toModel).collect(Collectors.toSet());
  }

  @Override
  public Boolean areFriends(String ownerId, String friendId) {
    if (ownerId.equals(friendId)) {
      return false;
    }

    Optional<FriendshipSchema> schemaOptional =
        jpaFriendShipRepository.findConfirmedFriendship(ownerId, friendId);

    return schemaOptional.isPresent();
  }
}
