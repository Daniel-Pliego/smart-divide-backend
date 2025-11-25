package mr.limpios.smart_divide_backend.aplication.listeners;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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
import mr.limpios.smart_divide_backend.domain.events.ExpenseCreatedEvent;
import mr.limpios.smart_divide_backend.domain.models.Expense;
import mr.limpios.smart_divide_backend.domain.models.ExpenseBalance;
import mr.limpios.smart_divide_backend.domain.models.ExpenseGroupBalance;
import mr.limpios.smart_divide_backend.domain.models.Group;
import mr.limpios.smart_divide_backend.domain.models.User;

@ExtendWith(MockitoExtension.class)
class ExpenseEventListenerTest {

    @Mock
    private ExpenseGroupBalanceRepository balanceRepository;

    @Mock
    private ExpenseGroupBalanceService expenseGroupBalanceService;

    @InjectMocks
    private ExpenseEventListener expenseEventListener;

    @Test
    void handleExpenseCreated_newBalance_createsAndSaves() {
        User creditor = Instancio.create(User.class);
        User debtor = Instancio.create(User.class);
        Group group = Instancio.create(Group.class);
        BigDecimal amount = new BigDecimal("100.00");

        ExpenseBalance expBalance = Instancio.of(ExpenseBalance.class)
            .set(Select.field(ExpenseBalance::creditor), creditor)
            .set(Select.field(ExpenseBalance::debtor), debtor)
            .set(Select.field(ExpenseBalance::amountToPaid), amount)
            .create();

        Expense expense = Instancio.of(Expense.class)
            .set(Select.field(Expense::group), group)
            .set(Select.field(Expense::balances), List.of(expBalance))
            .create();

        ExpenseCreatedEvent event = new ExpenseCreatedEvent(expense);

        when(balanceRepository.findByCreditorAndDebtorAndGroup(creditor.id(), debtor.id(), group.id()))
            .thenReturn(Optional.empty());

        expenseEventListener.handleExpenseCreated(event);

        ArgumentCaptor<ExpenseGroupBalance> captor = ArgumentCaptor.forClass(ExpenseGroupBalance.class);
        verify(balanceRepository).saveExpenseGroupBalance(captor.capture());

        ExpenseGroupBalance savedBalance = captor.getValue();
        assertEquals(creditor, savedBalance.creditor());
        assertEquals(debtor, savedBalance.debtor());
        assertEquals(amount, savedBalance.amount());
        assertEquals(group, savedBalance.group());

        verify(expenseGroupBalanceService).normalize(eq(creditor), eq(debtor), eq(group));
    }

    @Test
    void handleExpenseCreated_existingBalance_updatesAndSaves() {
        User creditor = Instancio.create(User.class);
        User debtor = Instancio.create(User.class);
        Group group = Instancio.create(Group.class);
        
        BigDecimal newDebtAmount = new BigDecimal("50.00");
        BigDecimal existingDebtAmount = new BigDecimal("20.00");
        BigDecimal expectedTotal = new BigDecimal("70.00");

        ExpenseBalance expBalance = Instancio.of(ExpenseBalance.class)
            .set(Select.field(ExpenseBalance::creditor), creditor)
            .set(Select.field(ExpenseBalance::debtor), debtor)
            .set(Select.field(ExpenseBalance::amountToPaid), newDebtAmount)
            .create();

        Expense expense = Instancio.of(Expense.class)
            .set(Select.field(Expense::group), group)
            .set(Select.field(Expense::balances), List.of(expBalance))
            .create();

        ExpenseGroupBalance existingBalance = Instancio.of(ExpenseGroupBalance.class)
            .set(Select.field(ExpenseGroupBalance::creditor), creditor)
            .set(Select.field(ExpenseGroupBalance::debtor), debtor)
            .set(Select.field(ExpenseGroupBalance::amount), existingDebtAmount)
            .set(Select.field(ExpenseGroupBalance::group), group)
            .create();

        ExpenseCreatedEvent event = new ExpenseCreatedEvent(expense);

        when(balanceRepository.findByCreditorAndDebtorAndGroup(creditor.id(), debtor.id(), group.id()))
            .thenReturn(Optional.of(existingBalance));

        expenseEventListener.handleExpenseCreated(event);

        ArgumentCaptor<ExpenseGroupBalance> captor = ArgumentCaptor.forClass(ExpenseGroupBalance.class);
        verify(balanceRepository).saveExpenseGroupBalance(captor.capture());

        ExpenseGroupBalance savedBalance = captor.getValue();
        assertEquals(existingBalance.id(), savedBalance.id()); 
        assertEquals(expectedTotal, savedBalance.amount());

        verify(expenseGroupBalanceService).normalize(eq(creditor), eq(debtor), eq(group));
    }
}