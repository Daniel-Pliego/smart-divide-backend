package mr.limpios.smart_divide_backend.aplication.services;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.USER_NOT_FOUND;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import mr.limpios.smart_divide_backend.aplication.repositories.FriendshipRepository;
import mr.limpios.smart_divide_backend.aplication.repositories.UserRepository;
import mr.limpios.smart_divide_backend.domain.dto.FriendshipDTO;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceNotFoundException;
import mr.limpios.smart_divide_backend.domain.models.Friendship;
import mr.limpios.smart_divide_backend.domain.models.User;

@Service
public class FriendshipService {

  private final FriendshipRepository friendshipRepository;
  private final NotificationService notificationService;
  private final UserRepository userRepository;

  public FriendshipService(FriendshipRepository friendshipRepository, UserRepository userRepository,
      NotificationService notificationService) {
    this.friendshipRepository = friendshipRepository;
    this.userRepository = userRepository;
    this.notificationService = notificationService;
  }

  public void createFriendRequest(String fromUserId, String toUserId) {

    User fromUser = userRepository.getUserbyId(fromUserId);
    User toUser = userRepository.getUserbyId(toUserId);

    if (Objects.isNull(fromUser) || Objects.isNull(toUser)) {
      throw new ResourceNotFoundException(USER_NOT_FOUND);
    }

    Friendship friendship = new Friendship(null, fromUser, toUser, true);

    friendshipRepository.createFriendRequest(friendship);
    notificationService.notifyFriendshipRequest(fromUser, toUser);
  }

  public Set<FriendshipDTO> getAllFriendsFromUser(String userId) {
    User user = userRepository.getUserbyId(userId);

    if (Objects.isNull(user)) {
      throw new ResourceNotFoundException(USER_NOT_FOUND);
    }

    Set<Friendship> friendships = friendshipRepository.getAllFriendshipsByUserId(userId);

    return friendships.stream().map(friendship -> mapToFriendshipDTO(friendship, userId))
        .collect(Collectors.toSet());
  }

  private FriendshipDTO mapToFriendshipDTO(Friendship friendship, String currentUserId) {
    User otherUser = friendship.requester().id().equals(currentUserId) ? friendship.friend()
        : friendship.requester();

    return new FriendshipDTO(String.valueOf(friendship.id()), otherUser.name(),
        otherUser.lastName(), otherUser.photoUrl(), otherUser.email());
  }
}
