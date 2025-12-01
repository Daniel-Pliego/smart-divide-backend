package mr.limpios.smart_divide_backend.domain.events;

import mr.limpios.smart_divide_backend.domain.models.Friendship;

public record FriendRequestCreatedEvent(Friendship friendship) {}