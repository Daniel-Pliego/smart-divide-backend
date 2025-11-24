package mr.limpios.smart_divide_backend.aplication.listeners;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.NO_EXISTING_DEBTS_FOR_USER_PAIR;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.PAYMENT_AMOUNT_EXCEEDS_DEBT;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.AllArgsConstructor;
import mr.limpios.smart_divide_backend.aplication.repositories.ExpenseGroupBalanceRepository;
import mr.limpios.smart_divide_backend.aplication.services.ExpenseGroupBalanceService;
import mr.limpios.smart_divide_backend.domain.events.PaymentCreatedEvent;
import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;
import mr.limpios.smart_divide_backend.domain.models.ExpenseGroupBalance;
import mr.limpios.smart_divide_backend.domain.models.Payment;

@Component
@AllArgsConstructor
public class PaymentEventListener {
    private final ExpenseGroupBalanceRepository balanceRepository;
    private final ExpenseGroupBalanceService expenseGroupBalanceService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handlePaymentCreated(PaymentCreatedEvent event) {
        Payment payment = event.payment();
        String creditorId = payment.fromUser().id();
        String debtorId = payment.toUser().id();
        String groupId = payment.group().id();

        Optional<ExpenseGroupBalance> existingBalance = balanceRepository.findByCreditorAndDebtorAndGroup(
                creditorId,
                debtorId,
                groupId);
        if (existingBalance.isEmpty()) {
            throw new InvalidDataException(NO_EXISTING_DEBTS_FOR_USER_PAIR);
        }
        ExpenseGroupBalance balance = existingBalance.get();

        if (payment.amount().compareTo(balance.amount()) > 0) {
            throw new InvalidDataException(PAYMENT_AMOUNT_EXCEEDS_DEBT);
        }
        BigDecimal newAmount = balance.amount().subtract(payment.amount());
        ExpenseGroupBalance updatedBalance = new ExpenseGroupBalance(
                balance.id(),
                balance.creditor(),
                balance.debtor(),
                newAmount,
                balance.group());
        balanceRepository.saveExpenseGroupBalance(updatedBalance);

        expenseGroupBalanceService.normalize(
                payment.toUser(),
                payment.fromUser(),
                payment.group());
    }

}
