package mr.limpios.smart_divide_backend.application.dtos.Auth;

public record AuthenticatedDTO(String userId, String email, String name, String lastName,
        String photoUrl, String token) {

}
