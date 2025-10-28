package mr.limpios.smart_divide_backend.domain.models;

import java.util.List;

public record Group(
        String id,
        String name,
        String description,
        User owner,
        List<User> members) {}
