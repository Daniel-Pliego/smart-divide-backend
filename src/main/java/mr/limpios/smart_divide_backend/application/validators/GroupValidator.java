package mr.limpios.smart_divide_backend.application.validators;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.DATA_REQUIRED;

import mr.limpios.smart_divide_backend.application.dtos.CreateGroupDTO;
import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;

public class GroupValidator {

  private GroupValidator() {}

  public static void validate(CreateGroupDTO group) throws InvalidDataException {
    if (group.name().isBlank() || group.description().isBlank()) {
      throw new InvalidDataException(DATA_REQUIRED);
    }
  }
}
