package mr.limpios.smart_divide_backend.application.services;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.FRIENDSHIP_ALREADY_EXISTS;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.USER_NOT_FOUND;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import mr.limpios.smart_divide_backend.application.assemblers.FriendshipAssembler;
import mr.limpios.smart_divide_backend.application.dtos.FriendshipDTO;
import mr.limpios.smart_divide_backend.application.repositories.FriendshipRepository;
import mr.limpios.smart_divide_backend.application.repositories.UserRepository;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceExistException;
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

    if (friendshipRepository.areFriends(fromUserId, toUserId)) {
      throw new ResourceExistException(FRIENDSHIP_ALREADY_EXISTS);
    }

    friendshipRepository.createFriendRequest(friendship);
    notificationService.notifyFriendshipRequest(fromUser, toUser);
  }

  public Set<FriendshipDTO> getAllFriendsFromUser(String userId) {
    User user = userRepository.getUserbyId(userId);

    if (Objects.isNull(user)) {
      throw new ResourceNotFoundException(USER_NOT_FOUND);
    }

    Set<Friendship> friendships = friendshipRepository.getAllFriendshipsByUserId(userId);

    return friendships.stream()
        .map(friendship -> FriendshipAssembler.toFriendshipDTO(friendship, userId))
        .collect(Collectors.toSet());
  }
}
