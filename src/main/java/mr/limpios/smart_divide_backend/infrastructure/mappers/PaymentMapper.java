package mr.limpios.smart_divide_backend.infrastructure.mappers;

import mr.limpios.smart_divide_backend.domain.models.Payment;
import mr.limpios.smart_divide_backend.infrastructure.schemas.PaymentSchema;

public class PaymentMapper {

  public static Payment toModel(PaymentSchema paymentSchema) {
    return new Payment(paymentSchema.getId(), UserMapper.toModel(paymentSchema.getFromUser()),
        UserMapper.toModel(paymentSchema.getToUser()), paymentSchema.getAmount(),
        GroupMapper.toModel(paymentSchema.getGroup()), paymentSchema.getPaidWithCard(),
        paymentSchema.getCreatedAt());
  }

  public static PaymentSchema toSchema(Payment payment) {
    return PaymentSchema.builder().id(payment.id())
        .fromUser(UserMapper.toSchema(payment.fromUser()))
        .toUser(UserMapper.toSchema(payment.toUser())).amount(payment.amount())
        .group(GroupMapper.toSchema(payment.group())).createdAt(payment.createdAt())
        .paidWithCard(payment.paidByCard()).build();
  }
}
