package mr.limpios.smart_divide_backend.infraestructure.https.stripe;

public record PaymentIntentResponse(
        String paymentIntentClientSecret,
        String customerSessionClientSecret,
        String customerId
) {}
