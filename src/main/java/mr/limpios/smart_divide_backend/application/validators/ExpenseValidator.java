package mr.limpios.smart_divide_backend.application.validators;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.*;

import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import mr.limpios.smart_divide_backend.application.dtos.CreateExpenseParticipantDTO;
import mr.limpios.smart_divide_backend.application.dtos.ExpenseInputDTO;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceNotFoundException;
import mr.limpios.smart_divide_backend.domain.models.User;

public class ExpenseValidator {
  public static void validateGroupMembership(Map<String, User> membersMap, String userId,
      ExpenseInputDTO dto) {
    if (!membersMap.containsKey(userId)) {
      throw new ResourceNotFoundException(USER_NOT_MEMBER_OF_GROUP);
    }

    HashSet<String> memberIds = new HashSet<>(membersMap.keySet());

    HashSet<String> dtoParticipantsIds = dto.participants().stream()
        .map(CreateExpenseParticipantDTO::userId).collect(Collectors.toCollection(HashSet::new));

    HashSet<String> dtoPayersIds = dto.payers().stream().map(CreateExpenseParticipantDTO::userId)
        .collect(Collectors.toCollection(HashSet::new));

    if (!memberIds.containsAll(dtoParticipantsIds)) {
      throw new ResourceNotFoundException(DEBTORS_NOT_IN_GROUP);
    }

    if (!memberIds.containsAll(dtoPayersIds)) {
      throw new ResourceNotFoundException(PAYERS_NOT_IN_GROUP);
    }
  }
}
