package mr.limpios.smart_divide_backend.domain.dto;
public record UserDetailsDTO(
    String id,
    String name,
    String lastName,
    String email,
    String photoUrl,
    Boolean isVerified){}
