package mr.limpios.smart_divide_backend.infraestructure.dto;

public record AuthenticatedDTO(String userId, String email, String name, String fullName, String photoUrl, String token) {

}
