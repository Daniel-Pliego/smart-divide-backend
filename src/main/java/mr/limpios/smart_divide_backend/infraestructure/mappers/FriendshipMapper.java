package mr.limpios.smart_divide_backend.infraestructure.mappers;

import mr.limpios.smart_divide_backend.domain.models.Friendship;
import mr.limpios.smart_divide_backend.infraestructure.schemas.FriendshipSchema;


public class FriendshipMapper {
    private FriendshipMapper() {}

    public static FriendshipSchema toSchema(Friendship friendship) {

        return new FriendshipSchema(
                friendship.id(),
                UserMapper.toSchema(friendship.requester()),
                UserMapper.toSchema(friendship.friend()),
                friendship.confirmed()
        );
    }

    public static Friendship toModel(FriendshipSchema friendshipSchema) {

        return new Friendship(
                friendshipSchema.getId(),
                UserMapper.toModel(friendshipSchema.getRequester()),
                UserMapper.toModel(friendshipSchema.getFriend()),
                friendshipSchema.getConfirmed()
        );
    }
}
