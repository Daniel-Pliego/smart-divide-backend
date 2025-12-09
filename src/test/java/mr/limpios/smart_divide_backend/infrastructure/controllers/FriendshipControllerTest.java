package mr.limpios.smart_divide_backend.infrastructure.controllers;

import java.util.Set;

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

import mr.limpios.smart_divide_backend.application.services.FriendshipService;
import mr.limpios.smart_divide_backend.application.dtos.CreateFriendshipDTO;
import mr.limpios.smart_divide_backend.application.dtos.FriendshipDTO;
import mr.limpios.smart_divide_backend.infrastructure.security.JWTAuthorizationFilter;
import mr.limpios.smart_divide_backend.infrastructure.utils.SecurityTestUtils;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FriendshipController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JWTAuthorizationFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class FriendshipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FriendshipService friendshipService;

    @Test
    void createFriendRelationship_success() throws Exception {
        String userId = "user-1";

        SecurityTestUtils.mockAuthenticatedUser(userId);

        CreateFriendshipDTO inputDTO = Instancio.create(CreateFriendshipDTO.class);

        doNothing().when(friendshipService).createFriendRequest(anyString(), anyString());

        mockMvc.perform(post("/friendship")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ok").value(true))
                .andExpect(jsonPath("$.message").value("Friend request created successfully"));
    }

    @Test
    void getAllFriendsFromUser_success() throws Exception {
        String userId = "user-1";

        SecurityTestUtils.mockAuthenticatedUser(userId);

        Set<FriendshipDTO> responseSet = Instancio.ofSet(FriendshipDTO.class).create();

        when(friendshipService.getAllFriendsFromUser(userId)).thenReturn(responseSet);

        mockMvc.perform(get("/friendship")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true))
                .andExpect(jsonPath("$.message").value("Friendships retrieved successfully"))
                .andExpect(jsonPath("$.body").isArray());
    }
}