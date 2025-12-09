package mr.limpios.smart_divide_backend.application.services;

import mr.limpios.smart_divide_backend.application.repositories.UserRepository;
import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceExistException;
import mr.limpios.smart_divide_backend.domain.exceptions.UnauthorizedAccessException;
import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.infrastructure.security.JWTService;
import mr.limpios.smart_divide_backend.application.dtos.Auth.AuthenticatedDTO;
import mr.limpios.smart_divide_backend.application.dtos.Auth.UserSignInDTO;
import mr.limpios.smart_divide_backend.application.dtos.Auth.UserSignUpDTO;

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

import org.instancio.Instancio;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Should register a user successfully when all data is valid")
    void signUp_success() {
        UserSignUpDTO dto = new UserSignUpDTO("John", "Doe", "john@example.com", "pass123", "photo.jpg");

        when(userRepository.findUserByEmail(dto.email())).thenReturn(null);

        User savedUser = new User("uid-1", dto.name(), dto.lastName(), dto.email(), "encodedPass",
                dto.photoUrl(), false);
        when(userRepository.saveUser(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateAccessToken(savedUser.email())).thenReturn("jwt-token");

        AuthenticatedDTO result = authService.signUp(dto);

        assertEquals(savedUser.id(), result.userId());
        assertEquals(savedUser.email(), result.email());
        assertEquals(savedUser.name(), result.name());
        assertEquals(savedUser.lastName(), result.lastName());
        assertEquals("jwt-token", result.token());
    }

    @Test
    @DisplayName("Should throw ResourceExistException when email already exists")
    void signUp_emailExists_throwsResourceExistException() {
        UserSignUpDTO dto = new UserSignUpDTO("John", "Doe", "john@example.com", "pass123", "photo.jpg");

        when(userRepository.findUserByEmail(dto.email()))
                .thenReturn(new User("u1", "Other", "User", dto.email(), "pw", "url", false));

        assertThrows(ResourceExistException.class, () -> authService.signUp(dto));
    }

    @Test
    @DisplayName("Should throw InvalidDataException when sign-up data is missing or blank")
    void signUp_invalidData_throwsInvalidDataException() {
        UserSignUpDTO dto = new UserSignUpDTO("", "", "", "password", "");

        assertThrows(InvalidDataException.class, () -> authService.signUp(dto));

        verify(userRepository, never()).saveUser(any());
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when password not matches")
    void signIn_passwordNotMatches_throwsUnauthorizedException() {
        UserSignInDTO dto = new UserSignInDTO("", "wrongPass");

        when(userRepository.findUserByEmail(dto.email())).thenReturn(Instancio.create(User.class));
        assertThrows(UnauthorizedAccessException.class, () -> authService.signIn(dto));
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when user does not exist")
    void signIn_userNotExist_throwsUnauthorizedException() {
        UserSignInDTO dto = new UserSignInDTO("", "somePass");

        when(userRepository.findUserByEmail(dto.email())).thenReturn(null);

        assertThrows(UnauthorizedAccessException.class, () -> authService.signIn(dto));
    }

    @Test
    @DisplayName("Should sign in successfully when credentials are valid")
    void signIn_success() {
        UserSignInDTO dto = new UserSignInDTO("john@example.com", "correctPass");
        User existingUser = new User("uid-1", "John", "Doe", dto.email(), "correctPass",
                "photo.jpg", false);

        when(userRepository.findUserByEmail(dto.email())).thenReturn(existingUser);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtService.generateAccessToken(existingUser.email())).thenReturn("jwt-token");

        AuthenticatedDTO result = authService.signIn(dto);

        assertEquals(existingUser.id(), result.userId());
        assertEquals(existingUser.email(), result.email());
        assertEquals(existingUser.name(), result.name());
        assertEquals(existingUser.lastName(), result.lastName());
        assertEquals("jwt-token", result.token());
    }
}