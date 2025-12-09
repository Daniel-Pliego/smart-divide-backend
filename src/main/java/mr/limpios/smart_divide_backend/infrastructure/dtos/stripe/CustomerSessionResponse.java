package mr.limpios.smart_divide_backend.infrastructure.dtos.stripe;

public record CustomerSessionResponse(String customerSessionClientSecret,
        String customerId) {
    
}
