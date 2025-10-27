package mr.limpios.smart_divide_backend.infraestructure.repositories.impl;

import java.util.Objects;
import mr.limpios.smart_divide_backend.aplication.repositories.UserRepository;
import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.infraestructure.mappers.UserMapper;
import mr.limpios.smart_divide_backend.infraestructure.repositories.jpa.JPAUserRepository;
import mr.limpios.smart_divide_backend.infraestructure.schemas.UserSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImp implements UserRepository {
  @Autowired
  private JPAUserRepository jpaUserRepository;

  @Override
  public User getUserbyId(String id) {
    UserSchema userSchema = this.jpaUserRepository.findById(id).orElse(null);

    if (Objects.isNull(userSchema)) {
      return null;
    }

    return UserMapper.toModel(userSchema);
  }
}
