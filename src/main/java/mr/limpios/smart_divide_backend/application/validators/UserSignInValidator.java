package mr.limpios.smart_divide_backend.application.validators;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.MISSING_REQUIRED_FIELDS;

import mr.limpios.smart_divide_backend.application.dtos.Auth.UserSignInDTO;
import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;

public class UserSignInValidator {
  public static void validate(UserSignInDTO user) throws InvalidDataException {

    if (isAllRequiredDataFilled(user)) {
      throw new InvalidDataException(MISSING_REQUIRED_FIELDS);
    }
  }

  private static boolean isAllRequiredDataFilled(UserSignInDTO user) {
    return (user.email().isEmpty() && user.password().isEmpty());
  }
}
