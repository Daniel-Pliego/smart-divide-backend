package mr.limpios.smart_divide_backend.aplication.services;

import mr.limpios.smart_divide_backend.aplication.repositories.UserRepository;
import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceExistException;
import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.infraestructure.dto.AuthenticatedDTO;
import mr.limpios.smart_divide_backend.infraestructure.dto.UserSignUpDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Should register a user successfully when all data is valid")
    void signUp_success() {
        UserSignUpDTO dto =
                new UserSignUpDTO("John", "Doe", "john@example.com", "pass123", "photo.jpg");

        when(userRepository.findUserByEmail(dto.email())).thenReturn(null);

        User savedUser = new User("uid-1", dto.name(), dto.lastName(), dto.email(), "encodedPass",
                dto.photoUrl(), false, null);
        when(userRepository.saveUser(any(User.class))).thenReturn(savedUser);

        AuthenticatedDTO result = authService.signUp(dto);

        assertEquals(savedUser.id(), result.userId());
        assertEquals(savedUser.email(), result.email());
        assertEquals(savedUser.name(), result.name());
        assertEquals(savedUser.name() + " " + savedUser.lastName(), result.lastName());
    }

    @Test
    @DisplayName("Should throw ResourceExistException when email already exists")
    void signUp_emailExists_throwsResourceExistException() {
        UserSignUpDTO dto =
                new UserSignUpDTO("John", "Doe", "john@example.com", "pass123", "photo.jpg");

        when(userRepository.findUserByEmail(dto.email()))
                .thenReturn(new User("u1", "Other", "User", dto.email(), "pw", "url", false, null));

        assertThrows(ResourceExistException.class, () -> authService.signUp(dto));
    }

    @Test
    @DisplayName("Should throw InvalidDataException when sign-up data is missing or blank")
    void signUp_invalidData_throwsInvalidDataException() {
        UserSignUpDTO dto = new UserSignUpDTO("", "", "", "password", "");

        assertThrows(InvalidDataException.class, () -> authService.signUp(dto));

        verify(userRepository, never()).saveUser(any());
    }
}
