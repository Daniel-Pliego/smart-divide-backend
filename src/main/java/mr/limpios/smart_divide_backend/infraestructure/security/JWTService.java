package mr.limpios.smart_divide_backend.infraestructure.security;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.USER_NOT_FOUND;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import mr.limpios.smart_divide_backend.aplication.repositories.UserRepository;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceNotFoundException;
import mr.limpios.smart_divide_backend.domain.models.User;

@Component
public class JWTService {

  @Value("${jwt.secret.key}")
  private String secretKey;

  @Value("${jwt.expiration.time}")
  private String timeExpiration;

  @Autowired
  private UserRepository userRepository;

  public String generateAccessToken(String username) {
    return Jwts.builder().setSubject(username).setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(timeExpiration)))
        .signWith(getSignatureKey(), SignatureAlgorithm.HS256).compact();
  }

  public boolean isValidToken(String token) {
    boolean isValidToken = true;

    try {
      Jwts.parserBuilder().setSigningKey(getSignatureKey()).build().parseClaimsJws(token).getBody();
    } catch (Exception e) {
      isValidToken = false;
    }

    return isValidToken;
  }

  public String getUsernameFromToken(String token) {
    return getClaim(token, Claims::getSubject);
  }

  public String getUserIdFromToken(String token) {
    if (token.startsWith("Bearer ")) {
      token = token.substring(7);
    }

    String email = getUsernameFromToken(token);

    User user = userRepository.findUserByEmail(email);
    if (user == null) {
      throw new ResourceNotFoundException(USER_NOT_FOUND);
    }

    return user.id();

  }

  public <T> T getClaim(String token, Function<Claims, T> claimGetterFunction) {
    Claims claims = extractAllClaims(token);
    return claimGetterFunction.apply(claims);
  }

  public Claims extractAllClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(getSignatureKey()).build().parseClaimsJws(token)
        .getBody();
  }

  public Key getSignatureKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
