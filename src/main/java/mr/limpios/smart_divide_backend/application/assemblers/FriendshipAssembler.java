package mr.limpios.smart_divide_backend.application.assemblers;

import mr.limpios.smart_divide_backend.application.dtos.FriendshipDTO;
import mr.limpios.smart_divide_backend.domain.models.Friendship;
import mr.limpios.smart_divide_backend.domain.models.User;

public class FriendshipAssembler {

  public static FriendshipDTO toFriendshipDTO(Friendship friendship, String currentUserId) {
    User otherUser = friendship.requester().id().equals(currentUserId) ? friendship.friend()
        : friendship.requester();

    return new FriendshipDTO(otherUser.id(), otherUser.name(), otherUser.lastName(),
        otherUser.photoUrl(), otherUser.email());
  }
}
