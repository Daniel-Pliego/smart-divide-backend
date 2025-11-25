package mr.limpios.smart_divide_backend.domain.validators;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.DATA_REQUIRED;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.Test;

import mr.limpios.smart_divide_backend.domain.dto.CreateGroupDTO;
import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;

class GroupValidatorTest {

    @Test
    void validate_success() {
        CreateGroupDTO dto = Instancio.of(CreateGroupDTO.class)
            .set(Select.field(CreateGroupDTO::name), "Valid Name")
            .set(Select.field(CreateGroupDTO::description), "Valid Description")
            .create();

        assertDoesNotThrow(() -> GroupValidator.validate(dto));
    }

    @Test
    void validate_fail_blankName() {
        CreateGroupDTO dto = Instancio.of(CreateGroupDTO.class)
            .set(Select.field(CreateGroupDTO::name), "   ")
            .set(Select.field(CreateGroupDTO::description), "Valid Description")
            .create();

        InvalidDataException ex = assertThrows(InvalidDataException.class, 
            () -> GroupValidator.validate(dto));
        
        assertEquals(DATA_REQUIRED, ex.getMessage());
    }

    @Test
    void validate_fail_blankDescription() {
        CreateGroupDTO dto = Instancio.of(CreateGroupDTO.class)
            .set(Select.field(CreateGroupDTO::name), "Valid Name")
            .set(Select.field(CreateGroupDTO::description), "")
            .create();

        InvalidDataException ex = assertThrows(InvalidDataException.class, 
            () -> GroupValidator.validate(dto));
        
        assertEquals(DATA_REQUIRED, ex.getMessage());
    }
}