package mr.limpios.smart_divide_backend.aplication.services;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.USER_NOT_FOUND;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import mr.limpios.smart_divide_backend.aplication.repositories.UserRepository;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceNotFoundException;
import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.infraestructure.dto.CardDetailsDTO;
import mr.limpios.smart_divide_backend.infraestructure.dto.UserDetailsDTO;

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

    Set<CardDetailsDTO> cardDetails = Optional.ofNullable(user.cards())
        .map(cards -> cards.stream().map(card -> new CardDetailsDTO(card.id(), card.lastDigits(),
            card.brand(), card.expMonth(), card.expYear())).collect(Collectors.toSet()))
        .orElse(Set.of());

    UserDetailsDTO userDetailsDTO = new UserDetailsDTO(user.id(), user.name(), user.lastName(),
        user.email(), user.photoUrl(), user.isVerified(), cardDetails);

    return userDetailsDTO;
  }
}
