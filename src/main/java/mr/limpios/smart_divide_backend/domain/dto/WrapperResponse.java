package mr.limpios.smart_divide_backend.domain.dto;

public record WrapperResponse<T>(boolean ok, String message, T body) {}
