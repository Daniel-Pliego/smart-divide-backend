package mr.limpios.smart_divide_backend.infraestructure.repositories.impl;

import mr.limpios.smart_divide_backend.aplication.repositories.FriendshipRepository;
import mr.limpios.smart_divide_backend.infraestructure.repositories.jpa.JPAFriendShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FriendShipRepositoryImp implements FriendshipRepository {
  @Autowired
  private JPAFriendShipRepository jpaFriendShipRepository;
}
