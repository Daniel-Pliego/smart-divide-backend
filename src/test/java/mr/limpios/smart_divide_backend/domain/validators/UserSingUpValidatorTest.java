package mr.limpios.smart_divide_backend.application.validators;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.INVALID_EMAIL_FORMAT;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.MISSING_REQUIRED_FIELDS;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.Test;

import mr.limpios.smart_divide_backend.application.dtos.Auth.UserSignUpDTO;
import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;

class UserSingUpValidatorTest {

    @Test
    void validate_success() {
        UserSignUpDTO dto = Instancio.of(UserSignUpDTO.class)
            .set(Select.field(UserSignUpDTO::email), "test@example.com")
            .set(Select.field(UserSignUpDTO::name), "John")
            .set(Select.field(UserSignUpDTO::lastName), "Doe")
            .set(Select.field(UserSignUpDTO::password), "password123")
            .create();

        assertDoesNotThrow(() -> UserSingUpValidator.validate(dto));
    }

    @Test
    void validate_fail_missingName() {
        UserSignUpDTO dto = Instancio.of(UserSignUpDTO.class)
            .set(Select.field(UserSignUpDTO::name), "")
            .create();

        InvalidDataException ex = assertThrows(InvalidDataException.class, 
            () -> UserSingUpValidator.validate(dto));
        
        assertEquals(MISSING_REQUIRED_FIELDS, ex.getMessage());
    }

    @Test
    void validate_fail_missingLastName() {
        UserSignUpDTO dto = Instancio.of(UserSignUpDTO.class)
            .set(Select.field(UserSignUpDTO::name), "John")
            .set(Select.field(UserSignUpDTO::lastName), "")
            .create();

        InvalidDataException ex = assertThrows(InvalidDataException.class, 
            () -> UserSingUpValidator.validate(dto));
        
        assertEquals(MISSING_REQUIRED_FIELDS, ex.getMessage());
    }

    @Test
    void validate_fail_missingEmailAndPassword() {
        UserSignUpDTO dto = Instancio.of(UserSignUpDTO.class)
            .set(Select.field(UserSignUpDTO::name), "John")
            .set(Select.field(UserSignUpDTO::lastName), "Doe")
            .set(Select.field(UserSignUpDTO::email), "")
            .set(Select.field(UserSignUpDTO::password), "")
            .create();

        InvalidDataException ex = assertThrows(InvalidDataException.class, 
            () -> UserSingUpValidator.validate(dto));
        
        assertEquals(MISSING_REQUIRED_FIELDS, ex.getMessage());
    }

    @Test
    void validate_fail_invalidEmailFormat() {
        UserSignUpDTO dto = Instancio.of(UserSignUpDTO.class)
            .set(Select.field(UserSignUpDTO::name), "John")
            .set(Select.field(UserSignUpDTO::lastName), "Doe")
            .set(Select.field(UserSignUpDTO::password), "pass")
            .set(Select.field(UserSignUpDTO::email), "invalid-email")
            .create();

        InvalidDataException ex = assertThrows(InvalidDataException.class, 
            () -> UserSingUpValidator.validate(dto));
        
        assertEquals(INVALID_EMAIL_FORMAT, ex.getMessage());
    }
}