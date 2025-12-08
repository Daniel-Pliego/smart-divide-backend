package mr.limpios.smart_divide_backend.infraestructure.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import mr.limpios.smart_divide_backend.aplication.services.UserInfoService;
import mr.limpios.smart_divide_backend.domain.dto.UserDetailsDTO;
import mr.limpios.smart_divide_backend.infraestructure.security.JWTAuthorizationFilter;

@WebMvcTest(
    controllers = UserInfoController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = JWTAuthorizationFilter.class
    )
)
@AutoConfigureMockMvc(addFilters = false)
class UserInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserInfoService userInfoService;

    @Test
    void getUserInfo_success() throws Exception {
        String userId = "user-1";
        UserDetailsDTO responseDTO = Instancio.create(UserDetailsDTO.class);

        when(userInfoService.getUserInfo(userId)).thenReturn(responseDTO);
        mockMvc.perform(get("/user/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.ok").value(true))
            .andExpect(jsonPath("$.message").value("User info retrieved successfully"))
            .andExpect(jsonPath("$.body").exists());
    }
}