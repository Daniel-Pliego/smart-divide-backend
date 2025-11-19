package mr.limpios.smart_divide_backend.infraestructure.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter {
  private final JWTService jwtService;
  private final UserDetailsService userDetailsService;
  private final AntPathMatcher pathMatcher = new AntPathMatcher();

  private static final String[] PUBLIC_URLS = {"/auth/**", "/actuator/**", "/v3/api-docs/**",
      "/swagger-ui/**", "/swagger-ui.html", "/docs"};

  public JWTAuthorizationFilter(JWTService jwtService, UserDetailsServiceImpl userDetailsService) {
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    boolean isPublicPath = Arrays.stream(PUBLIC_URLS)
        .anyMatch(path -> pathMatcher.match(path, request.getRequestURI()));

    if (isPublicPath) {
      filterChain.doFilter(request, response);
      return;
    }

    String bearerToken = request.getHeader("Authorization");

    if (Objects.nonNull(bearerToken) && bearerToken.startsWith("Bearer ")) {
      String token = bearerToken.substring(7);

      if (jwtService.isValidToken(token)) {
        String username = jwtService.getUsernameFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      }
    }

    filterChain.doFilter(request, response);
  }
}
