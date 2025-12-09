package mr.limpios.smart_divide_backend.application.validators;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.MISSING_REQUIRED_FIELDS;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.Test;

import mr.limpios.smart_divide_backend.application.dtos.Auth.UserSignInDTO;
import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;

class UserSignInValidatorTest {

    @Test
    void validate_success() {
        UserSignInDTO dto = Instancio.of(UserSignInDTO.class)
            .set(Select.field(UserSignInDTO::email), "test@example.com")
            .set(Select.field(UserSignInDTO::password), "password123")
            .create();

        assertDoesNotThrow(() -> UserSignInValidator.validate(dto));
    }

    @Test
    void validate_fail_bothFieldsEmpty() {
        UserSignInDTO dto = Instancio.of(UserSignInDTO.class)
            .set(Select.field(UserSignInDTO::email), "")
            .set(Select.field(UserSignInDTO::password), "")
            .create();

        InvalidDataException ex = assertThrows(InvalidDataException.class, 
            () -> UserSignInValidator.validate(dto));
        
        assertEquals(MISSING_REQUIRED_FIELDS, ex.getMessage());
    }
}