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

import mr.limpios.smart_divide_backend.application.services.PaymentService;
import mr.limpios.smart_divide_backend.application.dtos.CreatePaymentDTO;
import mr.limpios.smart_divide_backend.infrastructure.security.JWTAuthorizationFilter;
import mr.limpios.smart_divide_backend.infrastructure.utils.SecurityTestUtils;

@WebMvcTest(controllers = PaymentController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JWTAuthorizationFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void createPayment_success() throws Exception {
        String userId = "user-1";
        String groupId = "group-1";

        SecurityTestUtils.mockAuthenticatedUser(userId);

        CreatePaymentDTO inputDTO = Instancio.create(CreatePaymentDTO.class);

        doNothing().when(paymentService).createPayment(eq(userId), eq(groupId), any(CreatePaymentDTO.class), eq(false));

        mockMvc.perform(post("/groups/{groupId}/payments", groupId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ok").value(true))
                .andExpect(jsonPath("$.message").value("Payment created successfully"));
    }
}