package mr.limpios.smart_divide_backend.infraestructure.mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import mr.limpios.smart_divide_backend.domain.models.Card;
import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.infraestructure.schemas.CardSchema;
import mr.limpios.smart_divide_backend.infraestructure.schemas.UserSchema;

public class UserMapper {

  private UserMapper() {}

  public static UserSchema toSchema(User user) {

    Set<CardSchema> schemaCards =
        user.cards().stream().map(CardMapper::toSchema).collect(Collectors.toSet());

    return new UserSchema(user.id(), user.name(), user.lastName(), user.email(), user.password(),
        user.photUrl(), user.isVerified(), schemaCards);
  }

  public static User toModel(UserSchema userSchema) {

    List<Card> modelCards =
        userSchema.getCards().stream().map(CardMapper::toModel).collect(Collectors.toList());

    return new User(userSchema.getId(), userSchema.getName(), userSchema.getLastName(),
        userSchema.getEmail(), userSchema.getPassword(), userSchema.getPhotoUrl(),
        userSchema.getIsVerified(), modelCards);
  }
}
