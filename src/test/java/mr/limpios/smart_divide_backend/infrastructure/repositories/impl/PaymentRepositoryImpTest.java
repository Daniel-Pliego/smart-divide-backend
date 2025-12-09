package mr.limpios.smart_divide_backend.infrastructure.repositories.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import mr.limpios.smart_divide_backend.domain.models.Payment;
import mr.limpios.smart_divide_backend.infrastructure.mappers.PaymentMapper;
import mr.limpios.smart_divide_backend.infrastructure.repositories.jpa.JPAPaymentRepository;
import mr.limpios.smart_divide_backend.infrastructure.schemas.PaymentSchema;

@ExtendWith(MockitoExtension.class)
class PaymentRepositoryImpTest {

    @Mock
    private JPAPaymentRepository jpaPaymentRepository;

    @InjectMocks
    private PaymentRepositoryImp paymentRepository;

    @Test
    void findByGroupId_success() {
        try (MockedStatic<PaymentMapper> mapperMock = Mockito.mockStatic(PaymentMapper.class)) {
            String groupId = "group-1";
            int listSize = 3;
            List<PaymentSchema> schemas = Instancio.ofList(PaymentSchema.class)
                .size(listSize)
                .create();
            
            when(jpaPaymentRepository.findByGroupId(groupId)).thenReturn(schemas);
            
            mapperMock.when(() -> PaymentMapper.toModel(any(PaymentSchema.class)))
                .thenAnswer(invocation -> Instancio.create(Payment.class));

            List<Payment> result = paymentRepository.findByGroupId(groupId);

            assertNotNull(result);
            assertEquals(listSize, result.size());
            verify(jpaPaymentRepository).findByGroupId(groupId);
        }
    }

    @Test
    void savePayment_success() {
        try (MockedStatic<PaymentMapper> mapperMock = Mockito.mockStatic(PaymentMapper.class)) {
            Payment paymentToSave = Instancio.create(Payment.class);
            PaymentSchema schemaToSave = Instancio.create(PaymentSchema.class);
            PaymentSchema savedSchema = Instancio.create(PaymentSchema.class);
            Payment savedPayment = Instancio.create(Payment.class);

            mapperMock.when(() -> PaymentMapper.toSchema(paymentToSave)).thenReturn(schemaToSave);
            
            when(jpaPaymentRepository.save(schemaToSave)).thenReturn(savedSchema);
            
            mapperMock.when(() -> PaymentMapper.toModel(savedSchema)).thenReturn(savedPayment);

            Payment result = paymentRepository.savePayment(paymentToSave);

            assertNotNull(result);
            assertEquals(savedPayment, result);
            verify(jpaPaymentRepository).save(schemaToSave);
        }
    }
}