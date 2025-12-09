package mr.limpios.smart_divide_backend.infrastructure.repositories.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.infrastructure.mappers.UserMapper;
import mr.limpios.smart_divide_backend.infrastructure.repositories.jpa.JPAStripeRepository;
import mr.limpios.smart_divide_backend.infrastructure.schemas.StripeUserSchema;

@Repository

public class StripeUserInfoRepository {
  @Autowired
  JPAStripeRepository stripeRepository;

  public StripeUserSchema getOrCreateStripeUser(User user) {
    return stripeRepository.findByUserId(user.id()).orElseGet(() -> stripeRepository
        .save(StripeUserSchema.builder().user(UserMapper.toSchema(user)).build()));
  }

  public String getStripeAccountId(String userId) {
    StripeUserSchema stripeUser = stripeRepository.findByUserId(userId).orElse(null);
    if (stripeUser == null) {
      return null;
    }
    return stripeUser.getStripeAccountId();
  }

  public String getStripeCustomerId(String userId) {
    StripeUserSchema stripeUser = stripeRepository.findByUserId(userId).orElse(null);

    if (stripeUser == null) {
      return null;
    }
    return stripeUser.getStripeCustomerId();
  }

  public StripeUserSchema save(StripeUserSchema user) {
    return stripeRepository.save(user);
  }

}
