package mr.limpios.smart_divide_backend.domain.models;

public record Friendship(Integer id, User requester, User friend, Boolean confirmed) {}
