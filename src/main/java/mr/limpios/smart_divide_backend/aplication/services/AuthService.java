package mr.limpios.smart_divide_backend.aplication.services;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.EMAIL_ALREADY_EXISTS;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.UNAUTHORIZED_ACCESS;

import java.util.Objects;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import mr.limpios.smart_divide_backend.aplication.repositories.UserRepository;
import mr.limpios.smart_divide_backend.domain.dto.Auth.AuthenticatedDTO;
import mr.limpios.smart_divide_backend.domain.dto.Auth.UserSignInDTO;
import mr.limpios.smart_divide_backend.domain.dto.Auth.UserSignUpDTO;
import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceExistException;
import mr.limpios.smart_divide_backend.domain.exceptions.UnauthorizedAccessException;
import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.domain.validators.UserSignInValidator;
import mr.limpios.smart_divide_backend.domain.validators.UserSingUpValidator;
import mr.limpios.smart_divide_backend.infraestructure.security.JWTService;

@Service
public class AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JWTService jwtService;

  public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
      JWTService jwtService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  public AuthenticatedDTO signUp(UserSignUpDTO user)
      throws ResourceExistException, InvalidDataException {

    UserSingUpValidator.validate(user);

    if (Objects.nonNull(userRepository.findUserByEmail(user.email()))) {
      throw new ResourceExistException(EMAIL_ALREADY_EXISTS);
    }

    User savedUser = userRepository.saveUser(new User(null, user.name(), user.lastName(),
        user.email(), passwordEncoder.encode(user.password()), user.photoUrl(), false, null));

    return new AuthenticatedDTO(savedUser.id(), savedUser.email(), savedUser.name(),
        savedUser.lastName(), savedUser.photoUrl(), jwtService.generateAccessToken(user.email()));
  }

  public AuthenticatedDTO signIn(UserSignInDTO user) {

    UserSignInValidator.validate(user);

    User existingUser = userRepository.findUserByEmail(user.email());

    if (Objects.isNull(existingUser)
        || !passwordEncoder.matches(user.password(), existingUser.password())) {
      throw new UnauthorizedAccessException(UNAUTHORIZED_ACCESS);
    }

    return new AuthenticatedDTO(existingUser.id(), existingUser.email(), existingUser.name(),
        existingUser.lastName(), existingUser.photoUrl(),
        jwtService.generateAccessToken(user.email()));
  }

}
