package mr.limpios.smart_divide_backend.infrastructure.repositories.impl;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mr.limpios.smart_divide_backend.application.repositories.UserRepository;
import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.infrastructure.mappers.UserMapper;
import mr.limpios.smart_divide_backend.infrastructure.repositories.jpa.JPAUserRepository;
import mr.limpios.smart_divide_backend.infrastructure.schemas.UserSchema;

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

  @Override
  public User findUserByEmail(String email) {
    UserSchema userSchema = this.jpaUserRepository.findByEmail(email);
    if (Objects.isNull(userSchema)) {
      return null;
    }
    return UserMapper.toModel(userSchema);
  }

  @Override
  public User saveUser(User newUser) {
    UserSchema userSchema = UserMapper.toSchema(newUser);
    UserSchema savedUserSchema = this.jpaUserRepository.save(userSchema);
    return UserMapper.toModel(savedUserSchema);
  }
}
