package mr.limpios.smart_divide_backend.domain.models;

public record User(String id, String name, String lastName, String email, String password, String photoUrl, Boolean isVerified) {
}
