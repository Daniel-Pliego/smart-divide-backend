package mr.limpios.smart_divide_backend.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import mr.limpios.smart_divide_backend.infrastructure.security.JWTAuthorizationFilter;

@Configuration
public class SecurityConfig {

  private static final String[] PUBLIC_URLS = {"/auth/**", "/actuator/**", "/v3/api-docs/**",
      "/swagger-ui/**", "/swagger-ui.html", "/docs", "/stripe/webhook/**"};

  private final JWTAuthorizationFilter jwtAuthorizationFilter;
  private final UserDetailsService userDetailsService;

  public SecurityConfig(JWTAuthorizationFilter jwtAuthorizationFilter,
      UserDetailsService userDetailsService) {
    this.jwtAuthorizationFilter = jwtAuthorizationFilter;
    this.userDetailsService = userDetailsService;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(HttpSecurity http,
      AuthenticationConfiguration configuration) throws Exception {

    AuthenticationManagerBuilder authBuilder =
        http.getSharedObject(AuthenticationManagerBuilder.class);

    authBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());

    return authBuilder.build();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http,
      AuthenticationManager authenticationManager) throws Exception {

    http.csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationManager(authenticationManager).authorizeHttpRequests(auth -> {
          auth.requestMatchers(PUBLIC_URLS).permitAll();
          auth.requestMatchers(request -> HttpMethod.OPTIONS.matches(request.getMethod()))
              .permitAll();
          auth.anyRequest().authenticated();
        }).addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
