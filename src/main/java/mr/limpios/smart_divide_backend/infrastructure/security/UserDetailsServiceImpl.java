package mr.limpios.smart_divide_backend.infrastructure.security;

import java.util.Collections;
import java.util.Objects;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mr.limpios.smart_divide_backend.application.repositories.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserRepository userRepository;

  public UserDetailsServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    mr.limpios.smart_divide_backend.domain.models.User userSchema =
        userRepository.findUserByEmail(username);

    if (Objects.isNull(userSchema)) {
      throw new UsernameNotFoundException("Username not found!");
    }

    return new CustomUserDetails(userSchema,
        Collections.singletonList(new SimpleGrantedAuthority("GENERIC")));
  }
}
