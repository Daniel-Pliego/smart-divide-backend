package mr.limpios.smart_divide_backend.domain.dto;

public record AuthenticatedDTO(String userId, String email, String name, String lastName,
        String photoUrl, String token) {

}
