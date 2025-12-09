package mr.limpios.smart_divide_backend.application.listeners;


import java.math.BigDecimal;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.AllArgsConstructor;
import mr.limpios.smart_divide_backend.application.events.PaymentCreatedEvent;
import mr.limpios.smart_divide_backend.application.repositories.ExpenseGroupBalanceRepository;
import mr.limpios.smart_divide_backend.application.services.ExpenseGroupBalanceService;
import mr.limpios.smart_divide_backend.domain.models.ExpenseGroupBalance;
import mr.limpios.smart_divide_backend.domain.models.Payment;

@Component
@AllArgsConstructor
public class PaymentEventListener {
  private final ExpenseGroupBalanceService expenseGroupBalanceService;
  private final ExpenseGroupBalanceRepository balanceRepository;

  @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
  public void handlePaymentCreated(PaymentCreatedEvent event) {
    Payment payment = event.payment();
    ExpenseGroupBalance balance = event.balance();

    BigDecimal newAmount = balance.amount().subtract(payment.amount());
    ExpenseGroupBalance updatedBalance = new ExpenseGroupBalance(balance.id(), balance.creditor(),
        balance.debtor(), newAmount, balance.group());
    balanceRepository.saveExpenseGroupBalance(updatedBalance);

    expenseGroupBalanceService.normalize(payment.toUser(), payment.fromUser(), payment.group());
  }

}
