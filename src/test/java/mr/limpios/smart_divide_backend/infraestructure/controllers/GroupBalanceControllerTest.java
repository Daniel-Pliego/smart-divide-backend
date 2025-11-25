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

import mr.limpios.smart_divide_backend.aplication.services.ExpenseGroupBalanceService;
import mr.limpios.smart_divide_backend.domain.dto.GetGroupBalancesDTO;
import mr.limpios.smart_divide_backend.infraestructure.security.JWTAuthorizationFilter;

@WebMvcTest(
    controllers = GroupBalanceController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = JWTAuthorizationFilter.class
    )
)
@AutoConfigureMockMvc(addFilters = false)
class GroupBalanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExpenseGroupBalanceService expenseGroupBalanceService;

    @Test
    void getAllBalancesByGroup_success() throws Exception {
        String groupId = "group-1";
        GetGroupBalancesDTO responseDTO = Instancio.create(GetGroupBalancesDTO.class);

        when(expenseGroupBalanceService.getAllBalancesByGroup(groupId)).thenReturn(responseDTO);

        mockMvc.perform(get("/groups/{groupId}/balances", groupId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.ok").value(true))
            .andExpect(jsonPath("$.message").value("Balances retrieved successfully"))
            .andExpect(jsonPath("$.body").exists());
    }
}