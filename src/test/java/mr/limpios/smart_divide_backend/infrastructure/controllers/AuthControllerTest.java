package mr.limpios.smart_divide_backend.infrastructure.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import mr.limpios.smart_divide_backend.application.services.AuthService;
import mr.limpios.smart_divide_backend.application.dtos.Auth.AuthenticatedDTO;
import mr.limpios.smart_divide_backend.application.dtos.Auth.UserSignInDTO;
import mr.limpios.smart_divide_backend.application.dtos.Auth.UserSignUpDTO;
import mr.limpios.smart_divide_backend.infrastructure.security.JWTAuthorizationFilter;

@WebMvcTest(
    controllers = AuthController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = JWTAuthorizationFilter.class
    )
)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @Test
    void signUp_success() throws Exception {
        UserSignUpDTO signUpDTO = Instancio.create(UserSignUpDTO.class);
        AuthenticatedDTO authenticatedDTO = Instancio.create(AuthenticatedDTO.class);

        when(authService.signUp(any(UserSignUpDTO.class))).thenReturn(authenticatedDTO);

        mockMvc.perform(post("/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.ok").value(true))
            .andExpect(jsonPath("$.message").value("User created"))
            .andExpect(jsonPath("$.body.token").value(authenticatedDTO.token()));
    }

    @Test
    void signIn_success() throws Exception {
        UserSignInDTO signInDTO = Instancio.create(UserSignInDTO.class);
        AuthenticatedDTO authenticatedDTO = Instancio.create(AuthenticatedDTO.class);

        when(authService.signIn(any(UserSignInDTO.class))).thenReturn(authenticatedDTO);

        mockMvc.perform(post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signInDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.ok").value(true))
            .andExpect(jsonPath("$.message").value("User authenticated"))
            .andExpect(jsonPath("$.body.token").value(authenticatedDTO.token()));
    }
}