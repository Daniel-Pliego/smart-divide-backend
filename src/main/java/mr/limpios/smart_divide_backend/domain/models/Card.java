package mr.limpios.smart_divide_backend.domain.models;

public record Card(
        String id,
        String lastDigits,
        String brand,
        String expMonth,
        String expYear,
        String type,
        String fundingMethod
        ) {}
