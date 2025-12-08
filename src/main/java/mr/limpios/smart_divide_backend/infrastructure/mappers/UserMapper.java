package mr.limpios.smart_divide_backend.infrastructure.mappers;

import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.infrastructure.schemas.UserSchema;

public class UserMapper {

  private UserMapper() {}

  public static UserSchema toSchema(User user) {
    return new UserSchema(user.id(), user.name(), user.lastName(), user.email(), user.password(),
        user.photoUrl(), user.isVerified());
  }

  public static User toModel(UserSchema userSchema) {
    return new User(userSchema.getId(), userSchema.getName(), userSchema.getLastName(),
        userSchema.getEmail(), userSchema.getPassword(), userSchema.getPhotoUrl(),
        userSchema.getIsVerified());
  }
}
