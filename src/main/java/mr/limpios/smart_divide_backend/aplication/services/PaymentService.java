package mr.limpios.smart_divide_backend.aplication.services;

import mr.limpios.smart_divide_backend.aplication.repositories.PaymentRepository;
import mr.limpios.smart_divide_backend.domain.models.Payment;
import mr.limpios.smart_divide_backend.infraestructure.dto.PaymentDetailDTO;
import mr.limpios.smart_divide_backend.infraestructure.dto.PaymentUserDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<PaymentDetailDTO> getPaymentsByGroup(String groupId) {
        List<Payment> payments = paymentRepository.findByGroupId(groupId);

        int index = 1;
        List<PaymentDetailDTO> result = new ArrayList<>();

        for (Payment payment : payments) {
            result.add(buildPaymentDetailDTO(payment, index++));
        }

        return result;
    }

    private PaymentDetailDTO buildPaymentDetailDTO(Payment payment, int index) {

        PaymentUserDTO fromUser = new PaymentUserDTO(
                payment.fromUser().name(),
                payment.fromUser().lastName()
        );

        PaymentUserDTO toUser = new PaymentUserDTO(
                payment.toUser().name(),
                payment.toUser().lastName()
        );

        return new PaymentDetailDTO(
                index,
                fromUser,
                toUser,
                payment.amount(),
                payment.createdAt()
        );
    }
}