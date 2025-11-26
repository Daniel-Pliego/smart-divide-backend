package mr.limpios.smart_divide_backend.infraestructure.repositories.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.infraestructure.mappers.UserMapper;
import mr.limpios.smart_divide_backend.infraestructure.repositories.jpa.JPAUserRepository;
import mr.limpios.smart_divide_backend.infraestructure.schemas.UserSchema;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImpTest {

    @Mock
    private JPAUserRepository jpaUserRepository;

    @InjectMocks
    private UserRepositoryImp userRepository;

    @Test
    void getUserbyId_found_returnsUser() {
        try (MockedStatic<UserMapper> mapperMock = Mockito.mockStatic(UserMapper.class)) {
            String userId = "user-123";
            UserSchema schema = Instancio.create(UserSchema.class);
            User model = Instancio.create(User.class);

            when(jpaUserRepository.findById(userId)).thenReturn(Optional.of(schema));
            mapperMock.when(() -> UserMapper.toModel(schema)).thenReturn(model);

            User result = userRepository.getUserbyId(userId);

            assertNotNull(result);
            assertEquals(model, result);
        }
    }

    @Test
    void getUserbyId_notFound_returnsNull() {
        String userId = "user-999";
        when(jpaUserRepository.findById(userId)).thenReturn(Optional.empty());

        User result = userRepository.getUserbyId(userId);

        assertNull(result);
    }

    @Test
    void findUserByEmail_found_returnsUser() {
        try (MockedStatic<UserMapper> mapperMock = Mockito.mockStatic(UserMapper.class)) {
            String email = "test@example.com";
            UserSchema schema = Instancio.create(UserSchema.class);
            User model = Instancio.create(User.class);

            when(jpaUserRepository.findByEmail(email)).thenReturn(schema);
            mapperMock.when(() -> UserMapper.toModel(schema)).thenReturn(model);

            User result = userRepository.findUserByEmail(email);

            assertNotNull(result);
            assertEquals(model, result);
        }
    }

    @Test
    void findUserByEmail_notFound_returnsNull() {
        String email = "missing@example.com";
        when(jpaUserRepository.findByEmail(email)).thenReturn(null);

        User result = userRepository.findUserByEmail(email);

        assertNull(result);
    }

    @Test
    void saveUser_success_returnsSavedUser() {
        try (MockedStatic<UserMapper> mapperMock = Mockito.mockStatic(UserMapper.class)) {
            User newUser = Instancio.create(User.class);
            UserSchema schema = Instancio.create(UserSchema.class);
            UserSchema savedSchema = Instancio.create(UserSchema.class);
            User savedModel = Instancio.create(User.class);

            mapperMock.when(() -> UserMapper.toSchema(newUser)).thenReturn(schema);
            when(jpaUserRepository.save(schema)).thenReturn(savedSchema);
            mapperMock.when(() -> UserMapper.toModel(savedSchema)).thenReturn(savedModel);

            User result = userRepository.saveUser(newUser);

            assertNotNull(result);
            assertEquals(savedModel, result);
            verify(jpaUserRepository).save(schema);
        }
    }
}