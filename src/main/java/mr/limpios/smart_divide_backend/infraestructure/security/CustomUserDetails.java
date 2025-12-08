package mr.limpios.smart_divide_backend.infraestructure.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import mr.limpios.smart_divide_backend.domain.models.User;

@Getter
public class CustomUserDetails implements UserDetails {

  private final String userId;
  private final String username;
  private final String password;
  private final Collection<? extends GrantedAuthority> authorities;
  private final boolean isEnabled;

  public CustomUserDetails(User user, Collection<? extends GrantedAuthority> authorities) {
    this.userId = user.id();
    this.username = user.email();
    this.password = user.password();
    this.authorities = authorities;
    this.isEnabled = true;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return isEnabled;
  }
}
