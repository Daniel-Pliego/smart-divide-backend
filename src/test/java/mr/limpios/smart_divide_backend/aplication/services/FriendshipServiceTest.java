package mr.limpios.smart_divide_backend.aplication.services;

import static org.mockito.Mockito.when;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mr.limpios.smart_divide_backend.aplication.repositories.FriendshipRepository;
import mr.limpios.smart_divide_backend.aplication.repositories.UserRepository;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceNotFoundException;
import mr.limpios.smart_divide_backend.domain.models.Friendship;
import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.domain.dto.FriendshipDTO;
import static org.instancio.Select.field;

@ExtendWith(MockitoExtension.class)
public class FriendshipServiceTest {

    @Mock
    private FriendshipRepository friendshipRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private FriendshipService friendshipService;

    @Test
    @DisplayName("Get all friends from user - Success")
    public void getAllFriendsFromUser_success() {

        User user = Instancio.create(User.class);
        Set<Friendship> friendshipsDTO = Instancio.ofSet(Friendship.class)
                .size(3)
                .create();
        Friendship friendship = new Friendship(
                1,
                Instancio.of(User.class).set(field("id"), "user-id").create(),
                Instancio.create(User.class),
                true);

        friendshipsDTO.add(friendship);

        when(userRepository.getUserbyId("user-id"))
                .thenReturn(user);

        when(friendshipRepository.getAllFriendshipsByUserId("user-id"))
                .thenReturn(friendshipsDTO);

        Set<FriendshipDTO> result = friendshipService.getAllFriendsFromUser("user-id");

        assertEquals(friendshipsDTO.size(), result.size());
    }

    @Test
    @DisplayName("Get all friends from user with no friends - Success")
    public void getAllFriendsFromUser_noFriends_success() {

        User user = Instancio.create(User.class);
        Set<Friendship> friendshipsDTO = Instancio.ofSet(Friendship.class)
                .size(0)
                .create();

        when(userRepository.getUserbyId("user-id"))
                .thenReturn(user);

        when(friendshipRepository.getAllFriendshipsByUserId("user-id"))
                .thenReturn(friendshipsDTO);

        Set<FriendshipDTO> result = friendshipService.getAllFriendsFromUser("user-id");

        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Get all friends from non-existing user - Throws ResourceNotFoundException")
    public void getAllFriendsFromUser_nonExistingUser_throwsResourceNotFoundException() {
        when(userRepository.getUserbyId("non-existing-user-id"))
                .thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> friendshipService.getAllFriendsFromUser("non-existing-user-id"));
    }

    @Test
    @DisplayName("Create friendship successfully")
    public void createFriendship_success() {
        User requester = Instancio.create(User.class);
        User friend = Instancio.create(User.class);

        when(userRepository.getUserbyId("requester-id"))
                .thenReturn(requester);

        when(userRepository.getUserbyId("friend-id"))
                .thenReturn(friend);

        assertDoesNotThrow(() -> friendshipService.createFriendRequest("requester-id", "friend-id"));
    }

    @Test
    @DisplayName("Create friendship with non-existing user - Throws ResourceNotFoundException")
    public void createFriendship_nonExistingUser_throwsResourceNotFoundException() {
        User requester = Instancio.create(User.class);

        when(userRepository.getUserbyId("requester-id"))
                .thenReturn(requester);

        when(userRepository.getUserbyId("non-existing-friend-id"))
                .thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> friendshipService.createFriendRequest("requester-id", "non-existing-friend-id"));
    }

    @Test
    @DisplayName("Create friendship with non-existing requester - Throws ResourceNotFoundException")
    public void createFriendship_nonExistingRequester_throwsResourceNotFoundException() {

        when(userRepository.getUserbyId("non-existing-requester-id"))
                .thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> friendshipService.createFriendRequest("non-existing-requester-id", "friend-id"));
    }

}
