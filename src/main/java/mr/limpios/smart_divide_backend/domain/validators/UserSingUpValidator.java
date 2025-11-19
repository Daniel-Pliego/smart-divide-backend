package mr.limpios.smart_divide_backend.domain.validators;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.INVALID_EMAIL_FORMAT;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.MISSING_REQUIRED_FIELDS;

import mr.limpios.smart_divide_backend.domain.dto.Auth.UserSignUpDTO;
import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;

public class UserSingUpValidator {

  public static void validate(UserSignUpDTO user) throws InvalidDataException {

    if (isAllRequiredDataFilled(user)) {
      throw new InvalidDataException(MISSING_REQUIRED_FIELDS);
    }

    if (!isEmailFormatValid(user.email())) {
      throw new InvalidDataException(INVALID_EMAIL_FORMAT);
    }
  }

  private static boolean isAllRequiredDataFilled(UserSignUpDTO user) {
    return (user.name().isEmpty() || user.lastName().isEmpty()
        || user.email().isEmpty() && user.password().isEmpty());
  }

  private static boolean isEmailFormatValid(String email) {
    String emailPattern = "^[\\w\\.-]+@[\\w\\.-]+\\.\\w+";
    return email.matches(emailPattern);
  }
}
