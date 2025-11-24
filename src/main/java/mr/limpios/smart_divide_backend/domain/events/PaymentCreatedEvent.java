package mr.limpios.smart_divide_backend.domain.events;

import mr.limpios.smart_divide_backend.domain.models.Payment;

public record PaymentCreatedEvent(Payment payment) {
} 