package mr.limpios.smart_divide_backend.domain.validators;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.AMOUNT_MUST_BE_POSITIVE;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.BOTH_USERS_MUST_BE_MEMBERS_OF_GROUP;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.PAYER_NOT_THE_SAME_AS_AUTHENTICATED_USER;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.USER_CANNOT_PAY_SELF;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import mr.limpios.smart_divide_backend.domain.dto.CreatePaymentDTO;
import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;
import mr.limpios.smart_divide_backend.domain.models.Group;

class PaymentValidatorTest {

    @Test
    void validate_success() {
        String userId = "user-1";
        String receiverId = "user-2";
        CreatePaymentDTO dto = new CreatePaymentDTO(userId, receiverId, BigDecimal.TEN);
        Group group = mock(Group.class);

        when(group.hasMember(anyString())).thenReturn(true);

        assertDoesNotThrow(() -> PaymentValidator.validate(dto, userId, group));
    }

    @Test
    void validate_fail_payerMismatch() {
        String userId = "user-1";
        CreatePaymentDTO dto = new CreatePaymentDTO("other-user", "user-2", BigDecimal.TEN);
        Group group = mock(Group.class);

        InvalidDataException ex = assertThrows(InvalidDataException.class, 
            () -> PaymentValidator.validate(dto, userId, group));

        assertEquals(PAYER_NOT_THE_SAME_AS_AUTHENTICATED_USER, ex.getMessage());
    }

    @Test
    void validate_fail_paySelf() {
        String userId = "user-1";
        CreatePaymentDTO dto = new CreatePaymentDTO(userId, userId, BigDecimal.TEN);
        Group group = mock(Group.class);

        InvalidDataException ex = assertThrows(InvalidDataException.class, 
            () -> PaymentValidator.validate(dto, userId, group));

        assertEquals(USER_CANNOT_PAY_SELF, ex.getMessage());
    }

    @Test
    void validate_fail_negativeAmount() {
        String userId = "user-1";
        CreatePaymentDTO dto = new CreatePaymentDTO(userId, "user-2", new BigDecimal("-10"));
        Group group = mock(Group.class);

        InvalidDataException ex = assertThrows(InvalidDataException.class, 
            () -> PaymentValidator.validate(dto, userId, group));

        assertEquals(AMOUNT_MUST_BE_POSITIVE, ex.getMessage());
    }

    @Test
    void validate_fail_zeroAmount() {
        String userId = "user-1";
        CreatePaymentDTO dto = new CreatePaymentDTO(userId, "user-2", BigDecimal.ZERO);
        Group group = mock(Group.class);

        InvalidDataException ex = assertThrows(InvalidDataException.class, 
            () -> PaymentValidator.validate(dto, userId, group));

        assertEquals(AMOUNT_MUST_BE_POSITIVE, ex.getMessage());
    }

    @Test
    void validate_fail_notGroupMembers() {
        String userId = "user-1";
        CreatePaymentDTO dto = new CreatePaymentDTO(userId, "user-2", BigDecimal.TEN);
        Group group = mock(Group.class);

        when(group.hasMember(userId)).thenReturn(true);
        when(group.hasMember("user-2")).thenReturn(false);

        InvalidDataException ex = assertThrows(InvalidDataException.class, 
            () -> PaymentValidator.validate(dto, userId, group));

        assertEquals(BOTH_USERS_MUST_BE_MEMBERS_OF_GROUP, ex.getMessage());
    }
}