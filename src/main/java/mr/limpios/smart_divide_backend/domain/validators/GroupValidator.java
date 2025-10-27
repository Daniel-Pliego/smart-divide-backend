package mr.limpios.smart_divide_backend.domain.validators;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.DATA_REQUIRED;

import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;
import mr.limpios.smart_divide_backend.infraestructure.dto.CreateGroupDTO;

public class GroupValidator {

  private GroupValidator() {}

  public static void validate(CreateGroupDTO group) throws InvalidDataException {
    if (group.name().isBlank() || group.description().isBlank() || group.iconId() <= 0) {
      throw new InvalidDataException(DATA_REQUIRED);
    }
  }
}
