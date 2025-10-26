package mr.limpios.smart_divide_backend.infraestructure.repositories.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mr.limpios.smart_divide_backend.aplication.repositories.UserRepository;
import mr.limpios.smart_divide_backend.infraestructure.repositories.jpa.JPAUserRepository;

@Repository
public class UserRepositoryImp implements UserRepository {
    @Autowired
    private JPAUserRepository jpaUserRepository;
}
