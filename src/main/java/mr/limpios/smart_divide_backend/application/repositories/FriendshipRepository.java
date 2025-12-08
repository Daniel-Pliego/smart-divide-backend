package mr.limpios.smart_divide_backend.application.repositories;

import java.util.Set;

import mr.limpios.smart_divide_backend.domain.models.Friendship;

public interface FriendshipRepository {

  public void createFriendRequest(Friendship friendship);

  public Set<Friendship> getAllFriendshipsByUserId(String userId);

  Boolean areFriends(String ownerId, String friendId);
}
