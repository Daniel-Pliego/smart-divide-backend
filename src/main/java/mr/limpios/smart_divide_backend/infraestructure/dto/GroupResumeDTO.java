package mr.limpios.smart_divide_backend.infraestructure.dto;

public record GroupResumeDTO(
        String id,
        String name,
        String description,
        String ownerId,
        Integer totalDebts,
        Integer totalCredits) {}
