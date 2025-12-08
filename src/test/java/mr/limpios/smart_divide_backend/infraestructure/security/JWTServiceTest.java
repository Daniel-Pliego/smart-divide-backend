package mr.limpios.smart_divide_backend.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.Key;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

class JWTServiceTest {

    private JWTService jwtService;
    private String secretKey;

    @BeforeEach
    void setUp() {
        jwtService = new JWTService();
        
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        secretKey = Encoders.BASE64.encode(key.getEncoded());

        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "timeExpiration", "3600000");
    }

    @Test
    void generateAccessToken_success() {
        String username = "testUser";
        String token = jwtService.generateAccessToken(username);

        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    void isValidToken_validToken_returnsTrue() {
        String username = "testUser";
        String token = jwtService.generateAccessToken(username);

        boolean result = jwtService.isValidToken(token);

        assertTrue(result);
    }

    @Test
    void isValidToken_invalidToken_returnsFalse() {
        String invalidToken = "invalid.token.string";

        boolean result = jwtService.isValidToken(invalidToken);

        assertFalse(result);
    }

    @Test
    void isValidToken_tamperedToken_returnsFalse() {
        String token = jwtService.generateAccessToken("testUser");
        String tamperedToken = token + "tampered";

        boolean result = jwtService.isValidToken(tamperedToken);

        assertFalse(result);
    }

    @Test
    void getUsernameFromToken_success() {
        String username = "testUser";
        String token = jwtService.generateAccessToken(username);

        String extractedUsername = jwtService.getUsernameFromToken(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    void getSignatureKey_returnsCorrectKey() {
        Key key = jwtService.getSignatureKey();
        assertNotNull(key);
        assertEquals("HmacSHA256", key.getAlgorithm());
    }
}