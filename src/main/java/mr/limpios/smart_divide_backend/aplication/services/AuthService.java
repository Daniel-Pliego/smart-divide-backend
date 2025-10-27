package mr.limpios.smart_divide_backend.aplication.services;

import java.util.Objects;

import mr.limpios.smart_divide_backend.aplication.repositories.UserRepository;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.EMAIL_ALREADY_EXIST;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceExistException;
import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.domain.validators.UserSingUpValidator;
import mr.limpios.smart_divide_backend.infraestructure.dto.AuthenticatedDTO;
import mr.limpios.smart_divide_backend.infraestructure.dto.UserSignUpDTO;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthenticatedDTO signUp(UserSignUpDTO user) {

        User userToSave = new User(
                null,
                user.name(),
                user.lastName(),
                user.email(),
                passwordEncoder.encode(user.password()),
                user.photoUrl(),
                false,
                null);

        UserSingUpValidator.validate(userToSave);

        if (Objects.nonNull(userRepository.findUserByEmail(userToSave.email()))) {
            throw new ResourceExistException(EMAIL_ALREADY_EXIST);
        }

        User savedUser = userRepository.saveUser(userToSave);

        return new AuthenticatedDTO(
                savedUser.id(),
                savedUser.email(),
                savedUser.name(),
                savedUser.name() + " " + savedUser.lastName(),
                savedUser.photoUrl(),
                null
        );
    }

}
