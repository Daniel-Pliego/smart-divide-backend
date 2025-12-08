package mr.limpios.smart_divide_backend.application.validators;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.AMOUNT_MUST_BE_POSITIVE;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.BOTH_USERS_MUST_BE_MEMBERS_OF_GROUP;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.PAYER_NOT_THE_SAME_AS_AUTHENTICATED_USER;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.USER_CANNOT_PAY_SELF;

import java.math.BigDecimal;

import mr.limpios.smart_divide_backend.application.dtos.CreatePaymentDTO;
import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;
import mr.limpios.smart_divide_backend.domain.models.Group;


public class PaymentValidator {

  private PaymentValidator() {}

  public static void validate(CreatePaymentDTO createPaymentDTO, String userId, Group group) {
    if (!userId.equals(createPaymentDTO.fromUserId())) {
      throw new InvalidDataException(PAYER_NOT_THE_SAME_AS_AUTHENTICATED_USER);
    }

    if (createPaymentDTO.fromUserId().equals(createPaymentDTO.toUserId())) {
      throw new InvalidDataException(USER_CANNOT_PAY_SELF);
    }

    if (createPaymentDTO.amount().compareTo(BigDecimal.ZERO) <= 0) {
      throw new InvalidDataException(AMOUNT_MUST_BE_POSITIVE);
    }

    if (!group.hasMember(createPaymentDTO.fromUserId())
        || !group.hasMember(createPaymentDTO.toUserId())) {
      throw new InvalidDataException(BOTH_USERS_MUST_BE_MEMBERS_OF_GROUP);
    }

  }

}
