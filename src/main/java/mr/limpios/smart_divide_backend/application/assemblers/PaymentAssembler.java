package mr.limpios.smart_divide_backend.application.assemblers;

import java.util.List;
import java.util.stream.Collectors;

import mr.limpios.smart_divide_backend.application.dtos.PaymentDetailDTO;
import mr.limpios.smart_divide_backend.application.dtos.PaymentUserDTO;
import mr.limpios.smart_divide_backend.domain.models.Payment;

public class PaymentAssembler {

  public static PaymentDetailDTO toPaymentDetailDTO(Payment payment) {
    PaymentUserDTO fromUser =
        new PaymentUserDTO(payment.fromUser().name(), payment.fromUser().lastName());

    PaymentUserDTO toUser =
        new PaymentUserDTO(payment.toUser().name(), payment.toUser().lastName());

    return new PaymentDetailDTO(payment.id(), fromUser, toUser, payment.amount(),
        payment.createdAt());
  }

  public static List<PaymentDetailDTO> toPaymentDetailDTOList(List<Payment> payments) {
    return payments.stream().map(PaymentAssembler::toPaymentDetailDTO).collect(Collectors.toList());
  }
}
