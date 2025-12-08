package mr.limpios.smart_divide_backend.infrastructure.components.stripe.models;

public record CustomerSessionResponse(String customerSessionClientSecret,
        String customerId) {
    
}
