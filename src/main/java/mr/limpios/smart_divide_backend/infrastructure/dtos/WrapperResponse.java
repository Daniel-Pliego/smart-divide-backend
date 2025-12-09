package mr.limpios.smart_divide_backend.infrastructure.dtos;

public record WrapperResponse<T>(boolean ok, String message, T body) {}
