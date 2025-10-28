package mr.limpios.smart_divide_backend.infraestructure.dto;

import java.util.Set;

public record UserDetailsDTO(
    String id,
    String name,
    String lastName,
    String email,
    String photoUrl,
    Boolean isVerified,
    Set<CardDetailsDTO> cards) {}
