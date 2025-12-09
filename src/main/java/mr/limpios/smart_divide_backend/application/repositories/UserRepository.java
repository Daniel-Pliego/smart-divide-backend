package mr.limpios.smart_divide_backend.application.repositories;

import mr.limpios.smart_divide_backend.domain.models.User;

public interface UserRepository {
  User getUserbyId(String id);

  User findUserByEmail(String email);

  User saveUser(User newUser);
}
