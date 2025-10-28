package mr.limpios.smart_divide_backend.infraestructure.mappers;

import mr.limpios.smart_divide_backend.domain.models.Friendship;
import mr.limpios.smart_divide_backend.infraestructure.schemas.FriendShipSchema;

public class FriendshipMapper {

  public static FriendShipSchema toSchema(Friendship friendship) {
    return new FriendShipSchema(friendship.id(), UserMapper.toSchema(friendship.requester()),
        UserMapper.toSchema(friendship.friend()), friendship.confirmed());
  }

  public static Friendship toModel(FriendShipSchema schema) {
    return new Friendship(schema.getId(), UserMapper.toModel(schema.getRequester()),
        UserMapper.toModel(schema.getFriend()), schema.getConfirmed());
  }
}
