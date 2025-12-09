package mr.limpios.smart_divide_backend.application.events;

import mr.limpios.smart_divide_backend.domain.models.ExpenseGroupBalance;
import mr.limpios.smart_divide_backend.domain.models.Payment;

public record PaymentCreatedEvent(Payment payment, ExpenseGroupBalance balance) {
}
