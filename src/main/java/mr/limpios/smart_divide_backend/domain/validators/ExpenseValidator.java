package mr.limpios.smart_divide_backend.domain.validators;

import mr.limpios.smart_divide_backend.domain.dto.ExpenseDebtorDTO;
import mr.limpios.smart_divide_backend.domain.dto.ExpenseInputDTO;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceNotFoundException;
import mr.limpios.smart_divide_backend.domain.models.User;

import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.*;

public class ExpenseValidator {
    public static void validateGroupMembership(Map<String, User> membersMap, String userId, ExpenseInputDTO dto) {
        if (!membersMap.containsKey(userId)) {
            throw new ResourceNotFoundException(USER_NOT_MEMBER_OF_GROUP);
        }

        HashSet<String> memberIds = new HashSet<>(membersMap.keySet());

        HashSet<String> dtoParticipantsIds = dto.participants().stream()
                .map(ExpenseDebtorDTO::debtorId)
                .collect(Collectors.toCollection(HashSet::new));

        HashSet<String> dtoPayersIds = dto.payers().stream()
                .map(ExpenseDebtorDTO::debtorId)
                .collect(Collectors.toCollection(HashSet::new));

        if (!memberIds.containsAll(dtoParticipantsIds)) {
            throw new ResourceNotFoundException(DEBTORS_NOT_IN_GROUP);
        }

        if (!memberIds.containsAll(dtoPayersIds)) {
            throw new ResourceNotFoundException(PAYERS_NOT_IN_GROUP);
        }
    }
}
