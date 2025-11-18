package mr.limpios.smart_divide_backend.domain.dto;

public record NewMemberDTO(
        String groupId,
        String memberId,
        String name,
        String lastName,
        String photoUrl) {
}
