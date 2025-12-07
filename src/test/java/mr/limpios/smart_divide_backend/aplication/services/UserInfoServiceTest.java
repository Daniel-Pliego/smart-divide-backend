package mr.limpios.smart_divide_backend.aplication.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mr.limpios.smart_divide_backend.aplication.repositories.UserRepository;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceNotFoundException;
import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.domain.dto.UserDetailsDTO;

@ExtendWith(MockitoExtension.class)
public class UserInfoServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserInfoService userInfoService;

    @Test
    @DisplayName("Should return user details with cards when user exists and has cards")
    void getUserInfo_withCards_success() {
        String userId = "123";
        User user = new User("123", "John", "Doe", "john@example.com", "password123", "photo.jpg", true);
        when(userRepository.getUserbyId(userId)).thenReturn(user);

        UserDetailsDTO result = userInfoService.getUserInfo(userId);

        assertEquals(user.id(), result.id());
        assertEquals(user.name(), result.name());
        assertEquals(user.lastName(), result.lastName());
        assertEquals(user.email(), result.email());
        assertEquals(user.photoUrl(), result.photoUrl());
        assertEquals(user.isVerified(), result.isVerified());

    }

    @Test
    @DisplayName("Should return user details without cards when user exists but has no cards")
    void getUserInfo_withoutCards_success() {
        String userId = "123";
        User user = new User("123", "John", "Doe", "john@example.com", "password123", "photo.jpg", true);

        when(userRepository.getUserbyId(userId)).thenReturn(user);

        UserDetailsDTO result = userInfoService.getUserInfo(userId);

        assertEquals(user.id(), result.id());
        assertEquals(user.name(), result.name());
        assertEquals(user.lastName(), result.lastName());
        assertEquals(user.email(), result.email());
        assertEquals(user.photoUrl(), result.photoUrl());
        assertEquals(user.isVerified(), result.isVerified());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user does not exist")
    void getUserInfo_userNotFound_throwsResourceNotFoundException() {
        String userId = "123";
        when(userRepository.getUserbyId(userId)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> userInfoService.getUserInfo(userId));
    }
}
