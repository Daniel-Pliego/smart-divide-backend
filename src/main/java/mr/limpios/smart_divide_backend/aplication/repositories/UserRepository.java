package mr.limpios.smart_divide_backend.aplication.repositories;

import mr.limpios.smart_divide_backend.domain.models.User;

public interface UserRepository {
    User getUserbyId(String id);
    User findUserByEmail(String email);
    User saveUser(User newUser);
}
