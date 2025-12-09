package mr.limpios.smart_divide_backend.infrastructure.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
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

import mr.limpios.smart_divide_backend.application.services.ExpenseService;
import mr.limpios.smart_divide_backend.application.dtos.ExpenseInputDTO;
import mr.limpios.smart_divide_backend.infrastructure.security.JWTAuthorizationFilter;
import mr.limpios.smart_divide_backend.infrastructure.utils.SecurityTestUtils;

@WebMvcTest(controllers = ExpenseController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JWTAuthorizationFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ExpenseService expenseService;

    @Test
    void addExpense_success() throws Exception {
        String userId = "user-1";
        String groupId = "group-1";

        SecurityTestUtils.mockAuthenticatedUser(userId);

        ExpenseInputDTO inputDTO = Instancio.create(ExpenseInputDTO.class);

        doNothing().when(expenseService).addExpense(any(ExpenseInputDTO.class), eq(userId), eq(groupId));

        mockMvc.perform(post("/groups/{groupId}/expense", groupId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ok").value(true))
                .andExpect(jsonPath("$.message").value("Expense created successfully"));
    }
}