package mr.limpios.smart_divide_backend.aplication.listeners;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;

import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mr.limpios.smart_divide_backend.aplication.repositories.ExpenseGroupBalanceRepository;
import mr.limpios.smart_divide_backend.aplication.services.ExpenseGroupBalanceService;
import mr.limpios.smart_divide_backend.domain.events.PaymentCreatedEvent;
import mr.limpios.smart_divide_backend.domain.models.ExpenseGroupBalance;
import mr.limpios.smart_divide_backend.domain.models.Group;
import mr.limpios.smart_divide_backend.domain.models.Payment;
import mr.limpios.smart_divide_backend.domain.models.User;

@ExtendWith(MockitoExtension.class)
class PaymentEventListenerTest {

    @Mock
    private ExpenseGroupBalanceService expenseGroupBalanceService;

    @Mock
    private ExpenseGroupBalanceRepository balanceRepository;

    @InjectMocks
    private PaymentEventListener paymentEventListener;

    @Test
    void handlePaymentCreated_updatesBalanceAndNormalizes() {
        BigDecimal initialDebt = new BigDecimal("100.00");
        BigDecimal paymentAmount = new BigDecimal("40.00");
        BigDecimal expectedNewBalance = new BigDecimal("60.00");

        User creditor = Instancio.create(User.class);
        User debtor = Instancio.create(User.class);
        Group group = Instancio.create(Group.class);

        Payment payment = Instancio.of(Payment.class)
            .set(Select.field(Payment::amount), paymentAmount)
            .set(Select.field(Payment::fromUser), debtor)
            .set(Select.field(Payment::toUser), creditor)
            .set(Select.field(Payment::group), group)
            .create();

        ExpenseGroupBalance balance = Instancio.of(ExpenseGroupBalance.class)
            .set(Select.field(ExpenseGroupBalance::amount), initialDebt)
            .set(Select.field(ExpenseGroupBalance::creditor), creditor)
            .set(Select.field(ExpenseGroupBalance::debtor), debtor)
            .set(Select.field(ExpenseGroupBalance::group), group)
            .create();

        PaymentCreatedEvent event = new PaymentCreatedEvent(payment, balance);

        paymentEventListener.handlePaymentCreated(event);

        ArgumentCaptor<ExpenseGroupBalance> captor = ArgumentCaptor.forClass(ExpenseGroupBalance.class);
        verify(balanceRepository).saveExpenseGroupBalance(captor.capture());

        ExpenseGroupBalance savedBalance = captor.getValue();
        assertEquals(balance.id(), savedBalance.id());
        assertEquals(expectedNewBalance, savedBalance.amount());
        assertEquals(creditor, savedBalance.creditor());
        assertEquals(debtor, savedBalance.debtor());

        verify(expenseGroupBalanceService).normalize(eq(creditor), eq(debtor), eq(group));
    }
}