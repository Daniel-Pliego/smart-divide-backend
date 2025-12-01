package mr.limpios.smart_divide_backend.domain.events;

import mr.limpios.smart_divide_backend.domain.models.Group;

public record UserAddedToGroupEvent(Group group, String addedUserId, String addedByUserId) {}