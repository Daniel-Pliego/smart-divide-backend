package mr.limpios.smart_divide_backend.domain.validators;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.DATA_REQUIRED;

import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;
import mr.limpios.smart_divide_backend.domain.dto.GroupDataDTO;

public class GroupValidator {

  private GroupValidator() {}

  public static void validate(GroupDataDTO group) throws InvalidDataException {
    if (group.name().isBlank() || group.description().isBlank()) {
      throw new InvalidDataException(DATA_REQUIRED);
    }
  }
}
