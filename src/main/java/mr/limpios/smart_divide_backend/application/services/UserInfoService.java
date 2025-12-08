package mr.limpios.smart_divide_backend.application.services;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.USER_NOT_FOUND;

import java.util.Objects;

import org.springframework.stereotype.Service;

import mr.limpios.smart_divide_backend.application.dtos.UserDetailsDTO;
import mr.limpios.smart_divide_backend.application.repositories.UserRepository;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceNotFoundException;
import mr.limpios.smart_divide_backend.domain.models.User;

@Service
public class UserInfoService {

  UserRepository userRepository;

  public UserInfoService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public UserDetailsDTO getUserInfo(String userId) {

    User user = userRepository.getUserbyId(userId);

    if (Objects.isNull(user)) {
      throw new ResourceNotFoundException(USER_NOT_FOUND);
    }

    UserDetailsDTO userDetailsDTO = new UserDetailsDTO(user.id(), user.name(), user.lastName(),
        user.email(), user.photoUrl(), user.isVerified());

    return userDetailsDTO;
  }
}
