package mr.limpios.smart_divide_backend.infraestructure.repositories.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import mr.limpios.smart_divide_backend.aplication.repositories.DeviceTokenRepository;
import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.infraestructure.mappers.UserMapper;
import mr.limpios.smart_divide_backend.infraestructure.repositories.jpa.JPAUserDeviceTokenRepository;
import mr.limpios.smart_divide_backend.infraestructure.schemas.UserDeviceTokenSchema;

@Repository
public class UserDeviceTokenRepositoryImpl implements DeviceTokenRepository {

  private final JPAUserDeviceTokenRepository jpaUserDeviceTokenRepository;

  public UserDeviceTokenRepositoryImpl(JPAUserDeviceTokenRepository jpaUserDeviceTokenRepository) {
    this.jpaUserDeviceTokenRepository = jpaUserDeviceTokenRepository;
  }

  @Override
  public void saveToken(User user, String token) {
    Optional<UserDeviceTokenSchema> existingToken =
        jpaUserDeviceTokenRepository.findByUserId(user.id());

    if (existingToken.isPresent()) {
      UserDeviceTokenSchema schema = existingToken.get();
      schema.setToken(token);
      jpaUserDeviceTokenRepository.save(schema);
    } else {
      UserDeviceTokenSchema newSchema =
          UserDeviceTokenSchema.builder().user(UserMapper.toSchema(user)).token(token).build();
      jpaUserDeviceTokenRepository.save(newSchema);
    }
  }

  public List<String> getTokensByUserIdIn(List<String> userIds) {
    return jpaUserDeviceTokenRepository.findByUserIdIn(userIds).stream()
        .map(UserDeviceTokenSchema::getToken).toList();
  }


}
