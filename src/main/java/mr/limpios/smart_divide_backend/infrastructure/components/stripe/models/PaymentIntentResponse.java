package mr.limpios.smart_divide_backend.infrastructure.components.stripe.models;

public record PaymentIntentResponse(
        String paymentIntentClientSecret,
        String customerSessionClientSecret,
        String customerId
) {}
