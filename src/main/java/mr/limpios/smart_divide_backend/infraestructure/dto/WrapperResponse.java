package mr.limpios.smart_divide_backend.infraestructure.dto;

public record WrapperResponse<T>(boolean ok, String message, T body) {
}