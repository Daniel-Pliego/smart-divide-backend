package mr.limpios.smart_divide_backend.infrastructure.utils;

import mr.limpios.smart_divide_backend.infrastructure.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SecurityTestUtils {

    public static void mockAuthenticatedUser(String userId) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        CustomUserDetails userDetails = mock(CustomUserDetails.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUserId()).thenReturn(userId);

        SecurityContextHolder.setContext(securityContext);
    }
}
