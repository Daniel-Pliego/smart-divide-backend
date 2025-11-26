package mr.limpios.smart_divide_backend.infraestructure.controllers;

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

import mr.limpios.smart_divide_backend.aplication.services.PaymentService;
import mr.limpios.smart_divide_backend.domain.dto.CreatePaymentDTO;
import mr.limpios.smart_divide_backend.infraestructure.security.JWTAuthorizationFilter;

@WebMvcTest(
    controllers = PaymentController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = JWTAuthorizationFilter.class
    )
)
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
        CreatePaymentDTO inputDTO = Instancio.create(CreatePaymentDTO.class);

        doNothing().when(paymentService).createPayment(eq(userId), eq(groupId), any(CreatePaymentDTO.class));

        mockMvc.perform(post("/user/{userId}/groups/{groupId}/payments", userId, groupId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.ok").value(true))
            .andExpect(jsonPath("$.message").value("Payment created successfully"));
    }
}