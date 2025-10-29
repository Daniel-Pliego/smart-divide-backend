package mr.limpios.smart_divide_backend.infraestructure.dto;

public record UpdatedGroupMembers(
        String groupId,
        String memberId,
        String name,
        String lastName,
        String photoUrl) {
}
