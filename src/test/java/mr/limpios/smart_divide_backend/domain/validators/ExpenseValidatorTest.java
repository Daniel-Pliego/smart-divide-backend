package mr.limpios.smart_divide_backend.domain.validators;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.DEBTORS_NOT_IN_GROUP;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.PAYERS_NOT_IN_GROUP;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.USER_NOT_MEMBER_OF_GROUP;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;

import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.Test;

import mr.limpios.smart_divide_backend.domain.dto.CreateExpenseParticipantDTO;
import mr.limpios.smart_divide_backend.domain.dto.ExpenseInputDTO;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceNotFoundException;
import mr.limpios.smart_divide_backend.domain.models.User;

class ExpenseValidatorTest {

    @Test
    void validateGroupMembership_success() {
        User u1 = Instancio.create(User.class);
        User u2 = Instancio.create(User.class);
        Map<String, User> membersMap = Map.of("u1", u1, "u2", u2);
        String creatorId = "u1";

        CreateExpenseParticipantDTO payer = new CreateExpenseParticipantDTO("u1", 100.0);
        CreateExpenseParticipantDTO participant = new CreateExpenseParticipantDTO("u2", 100.0);

        ExpenseInputDTO dto = Instancio.of(ExpenseInputDTO.class)
            .set(Select.field(ExpenseInputDTO::payers), List.of(payer))
            .set(Select.field(ExpenseInputDTO::participants), List.of(participant))
            .create();

        assertDoesNotThrow(() -> ExpenseValidator.validateGroupMembership(membersMap, creatorId, dto));
    }

    @Test
    void validateGroupMembership_creatorNotMember_throwsException() {
        Map<String, User> membersMap = Map.of("u1", Instancio.create(User.class));
        String creatorId = "unknown-user";
        ExpenseInputDTO dto = Instancio.create(ExpenseInputDTO.class);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, 
            () -> ExpenseValidator.validateGroupMembership(membersMap, creatorId, dto));
        
        assertEquals(USER_NOT_MEMBER_OF_GROUP, ex.getMessage());
    }

    @Test
    void validateGroupMembership_debtorNotMember_throwsException() {
        Map<String, User> membersMap = Map.of("u1", Instancio.create(User.class));
        String creatorId = "u1";

        CreateExpenseParticipantDTO validPayer = new CreateExpenseParticipantDTO("u1", 100.0);
        CreateExpenseParticipantDTO invalidParticipant = new CreateExpenseParticipantDTO("unknown-user", 100.0);

        ExpenseInputDTO dto = Instancio.of(ExpenseInputDTO.class)
            .set(Select.field(ExpenseInputDTO::payers), List.of(validPayer))
            .set(Select.field(ExpenseInputDTO::participants), List.of(invalidParticipant))
            .create();

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, 
            () -> ExpenseValidator.validateGroupMembership(membersMap, creatorId, dto));

        assertEquals(DEBTORS_NOT_IN_GROUP, ex.getMessage());
    }

    @Test
    void validateGroupMembership_payerNotMember_throwsException() {
        Map<String, User> membersMap = Map.of("u1", Instancio.create(User.class));
        String creatorId = "u1";

        CreateExpenseParticipantDTO invalidPayer = new CreateExpenseParticipantDTO("unknown-user", 100.0);
        CreateExpenseParticipantDTO validParticipant = new CreateExpenseParticipantDTO("u1", 100.0);

        ExpenseInputDTO dto = Instancio.of(ExpenseInputDTO.class)
            .set(Select.field(ExpenseInputDTO::payers), List.of(invalidPayer))
            .set(Select.field(ExpenseInputDTO::participants), List.of(validParticipant))
            .create();

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, 
            () -> ExpenseValidator.validateGroupMembership(membersMap, creatorId, dto));

        assertEquals(PAYERS_NOT_IN_GROUP, ex.getMessage());
    }
}