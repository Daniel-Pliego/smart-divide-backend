package mr.limpios.smart_divide_backend.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import mr.limpios.smart_divide_backend.application.repositories.UserRepository;
import mr.limpios.smart_divide_backend.domain.models.User;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsername_success() {
        String email = "test@example.com";
        User domainUser = Instancio.create(User.class);

        when(userRepository.findUserByEmail(email)).thenReturn(domainUser);

        UserDetails result = userDetailsService.loadUserByUsername(email);

        assertNotNull(result);
        assertEquals(domainUser.email(), result.getUsername());
        assertEquals(domainUser.password(), result.getPassword());
        assertTrue(result.isEnabled());
        assertTrue(result.isAccountNonExpired());
        assertTrue(result.isAccountNonLocked());
        assertTrue(result.isCredentialsNonExpired());
        assertTrue(result.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("GENERIC")));
    }

    @Test
    void loadUserByUsername_notFound_throwsException() {
        String email = "missing@example.com";

        when(userRepository.findUserByEmail(email)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
            () -> userDetailsService.loadUserByUsername(email));
    }
}