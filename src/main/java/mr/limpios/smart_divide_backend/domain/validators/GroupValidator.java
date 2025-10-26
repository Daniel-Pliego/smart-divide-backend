package mr.limpios.smart_divide_backend.domain.validators;

import mr.limpios.smart_divide_backend.domain.dto.CreateGroupDTO;
import mr.limpios.smart_divide_backend.domain.dto.GroupDTO;
import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.DATA_REQUIRED;

public class GroupValidator {

    private GroupValidator() {}

    public static void validate(CreateGroupDTO group) throws InvalidDataException {
        if (group.name().isBlank() || group.description().isBlank() || group.iconId() <= 0) {
            throw new InvalidDataException(DATA_REQUIRED);
        }
    }
}
