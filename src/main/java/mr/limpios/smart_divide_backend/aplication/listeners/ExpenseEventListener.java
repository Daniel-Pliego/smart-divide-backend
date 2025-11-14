package mr.limpios.smart_divide_backend.aplication.listeners;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.AllArgsConstructor;
import mr.limpios.smart_divide_backend.aplication.repositories.ExpenseGroupBalanceRepository;
import mr.limpios.smart_divide_backend.domain.events.ExpenseCreatedEvent;
import mr.limpios.smart_divide_backend.domain.models.Expense;
import mr.limpios.smart_divide_backend.domain.models.ExpenseBalance;
import mr.limpios.smart_divide_backend.domain.models.ExpenseGroupBalance;
import mr.limpios.smart_divide_backend.domain.models.Group;

@Component
@AllArgsConstructor
public class ExpenseEventListener {
    private final ExpenseGroupBalanceRepository balanceRepository;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleExpenseCreated(ExpenseCreatedEvent event) {
        Expense expense = event.expense();

        for (ExpenseBalance expBalance : expense.balances()) {
            updateOrCreateGroupBalance(expBalance, expense.group());
        }
    }

    private void updateOrCreateGroupBalance(ExpenseBalance expBalance, Group group) {
        String creditorId = expBalance.creditor().id();
        String debtorId = expBalance.debtor().id();
        String groupId = group.id();

        var existingBalance = balanceRepository.findByCreditorAndDebtorAndGroup(creditorId, debtorId, groupId);

        if (existingBalance.isPresent()) {
            ExpenseGroupBalance balance = existingBalance.get();
            BigDecimal newAmount = balance.amount().add(expBalance.amountToPaid());

            ExpenseGroupBalance updatedBalance = new ExpenseGroupBalance(
                    balance.id(),
                    balance.creditor(),
                    balance.debtor(),
                    newAmount,
                    balance.group());
            balanceRepository.saveExpenseGroupBalance(updatedBalance);
        } else {
            ExpenseGroupBalance newGlobalBalance = new ExpenseGroupBalance(
                    null,
                    expBalance.creditor(),
                    expBalance.debtor(),
                    expBalance.amountToPaid(),
                    group);
            balanceRepository.saveExpenseGroupBalance(newGlobalBalance);
        }
    }
}
