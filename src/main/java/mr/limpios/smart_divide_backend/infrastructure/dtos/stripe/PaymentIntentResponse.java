package mr.limpios.smart_divide_backend.infrastructure.dtos.stripe;

public record PaymentIntentResponse(
        String paymentIntentClientSecret,
        String customerSessionClientSecret,
        String customerId
) {}
