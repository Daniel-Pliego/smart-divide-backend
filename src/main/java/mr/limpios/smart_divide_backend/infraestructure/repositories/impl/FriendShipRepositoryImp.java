package mr.limpios.smart_divide_backend.infraestructure.repositories.impl;

import java.util.Set;
import java.util.stream.Collectors;

import mr.limpios.smart_divide_backend.domain.models.Friendship;
import mr.limpios.smart_divide_backend.infraestructure.mappers.FriendshipMapper;
import mr.limpios.smart_divide_backend.infraestructure.schemas.FriendshipSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mr.limpios.smart_divide_backend.aplication.repositories.FriendshipRepository;
import mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceExistException;
import mr.limpios.smart_divide_backend.infraestructure.repositories.jpa.JPAFriendShipRepository;

import java.util.Optional;

@Repository
public class FriendShipRepositoryImp implements FriendshipRepository {
  @Autowired
  private JPAFriendShipRepository jpaFriendShipRepository;

  @Override
  public void createFriendRequest(Friendship friendship) {
    String requesterId = friendship.requester().id();
    String friendId = friendship.friend().id();
    boolean exists = jpaFriendShipRepository.existsByRequesterIdAndFriendIdOrRequesterIdAndFriendId(
        requesterId, friendId, friendId, requesterId);
    if (exists) {
      throw new ResourceExistException(ExceptionsConstants.FRIENDSHIP_ALREADY_EXISTS);
    }

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

        Optional<FriendshipSchema> schemaOptional = jpaFriendShipRepository
                .findConfirmedFriendship(ownerId, friendId);

        return schemaOptional.isPresent();
    }
}
