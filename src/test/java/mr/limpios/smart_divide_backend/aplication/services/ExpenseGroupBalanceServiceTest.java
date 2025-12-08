package mr.limpios.smart_divide_backend.application.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mr.limpios.smart_divide_backend.application.repositories.ExpenseGroupBalanceRepository;
import mr.limpios.smart_divide_backend.application.dtos.GetGroupBalancesDTO;
import mr.limpios.smart_divide_backend.domain.models.ExpenseGroupBalance;
import mr.limpios.smart_divide_backend.domain.models.Group;
import mr.limpios.smart_divide_backend.domain.models.User;

@ExtendWith(MockitoExtension.class)
public class ExpenseGroupBalanceServiceTest {

        @Mock
        private ExpenseGroupBalanceRepository balanceRepository;

        @InjectMocks
        private ExpenseGroupBalanceService balanceService;

        @Test
        @DisplayName("Normalize balances: User 2 owes more to User 1 (Positive Net) - Saves simplified balance")
        public void normalize_positiveNetBalance_savesSimplifiedBalance() {
                User user1 = Instancio.create(User.class);
                User user2 = Instancio.create(User.class);
                Group group = Instancio.create(Group.class);

                ExpenseGroupBalance balance2to1 = new ExpenseGroupBalance(
                                1, user1, user2, new BigDecimal("100.00"), group);

                ExpenseGroupBalance balance1to2 = new ExpenseGroupBalance(
                                2, user2, user1, new BigDecimal("40.00"), group);

                when(balanceRepository.findByCreditorAndDebtorAndGroup(user1.id(), user2.id(), group.id()))
                                .thenReturn(Optional.of(balance2to1));
                when(balanceRepository.findByCreditorAndDebtorAndGroup(user2.id(), user1.id(), group.id()))
                                .thenReturn(Optional.of(balance1to2));

                balanceService.normalize(user1, user2, group);

                verify(balanceRepository).deleteExpenseGroupBalance(1);
                verify(balanceRepository).deleteExpenseGroupBalance(2);

                ArgumentCaptor<ExpenseGroupBalance> balanceCaptor = ArgumentCaptor.forClass(ExpenseGroupBalance.class);
                verify(balanceRepository).saveExpenseGroupBalance(balanceCaptor.capture());

                ExpenseGroupBalance savedBalance = balanceCaptor.getValue();
                assertEquals(user1, savedBalance.creditor()); // User 1 receives
                assertEquals(user2, savedBalance.debtor()); // User 2 pays
                assertEquals(new BigDecimal("60.00"), savedBalance.amount());
        }

        @Test
        @DisplayName("Normalize balances: User 1 owes more to User 2 (Negative Net) - Saves inverted simplified balance")
        public void normalize_negativeNetBalance_savesInvertedSimplifiedBalance() {
                User user1 = Instancio.create(User.class);
                User user2 = Instancio.create(User.class);
                Group group = Instancio.create(Group.class);

                ExpenseGroupBalance balance2to1 = new ExpenseGroupBalance(
                                1, user1, user2, new BigDecimal("20.00"), group);

                ExpenseGroupBalance balance1to2 = new ExpenseGroupBalance(
                                2, user2, user1, new BigDecimal("50.00"), group);

                when(balanceRepository.findByCreditorAndDebtorAndGroup(user1.id(), user2.id(), group.id()))
                                .thenReturn(Optional.of(balance2to1));
                when(balanceRepository.findByCreditorAndDebtorAndGroup(user2.id(), user1.id(), group.id()))
                                .thenReturn(Optional.of(balance1to2));

                balanceService.normalize(user1, user2, group);

                verify(balanceRepository).deleteExpenseGroupBalance(1);
                verify(balanceRepository).deleteExpenseGroupBalance(2);

                ArgumentCaptor<ExpenseGroupBalance> balanceCaptor = ArgumentCaptor.forClass(ExpenseGroupBalance.class);
                verify(balanceRepository).saveExpenseGroupBalance(balanceCaptor.capture());

                ExpenseGroupBalance savedBalance = balanceCaptor.getValue();
                assertEquals(user2, savedBalance.creditor()); // User 2 receives
                assertEquals(user1, savedBalance.debtor()); // User 1 pays
                assertEquals(new BigDecimal("30.00"), savedBalance.amount());
        }

        @Test
        @DisplayName("Normalize balances: Amounts cancel out (Zero Net) - Deletes old and saves nothing")
        public void normalize_zeroNetBalance_savesNothing() {
                User user1 = Instancio.create(User.class);
                User user2 = Instancio.create(User.class);
                Group group = Instancio.create(Group.class);

                ExpenseGroupBalance balance2to1 = new ExpenseGroupBalance(1, user1, user2, new BigDecimal("50.00"),
                                group);
                ExpenseGroupBalance balance1to2 = new ExpenseGroupBalance(2, user2, user1, new BigDecimal("50.00"),
                                group);

                when(balanceRepository.findByCreditorAndDebtorAndGroup(user1.id(), user2.id(), group.id()))
                                .thenReturn(Optional.of(balance2to1));
                when(balanceRepository.findByCreditorAndDebtorAndGroup(user2.id(), user1.id(), group.id()))
                                .thenReturn(Optional.of(balance1to2));

                balanceService.normalize(user1, user2, group);

                verify(balanceRepository).deleteExpenseGroupBalance(1);
                verify(balanceRepository).deleteExpenseGroupBalance(2);

                verify(balanceRepository, never()).saveExpenseGroupBalance(any());
        }

        @Test
        @DisplayName("Normalize balances: No existing balances - Does nothing")
        public void normalize_noExistingBalances_doesNothing() {
                User user1 = Instancio.create(User.class);
                User user2 = Instancio.create(User.class);
                Group group = Instancio.create(Group.class);

                when(balanceRepository.findByCreditorAndDebtorAndGroup(any(), any(), any()))
                                .thenReturn(Optional.empty());

                balanceService.normalize(user1, user2, group);

                verify(balanceRepository, never()).deleteExpenseGroupBalance(any());
                verify(balanceRepository, never()).saveExpenseGroupBalance(any());
        }

        @Test
        @DisplayName("Get all balances by group - Success")
        public void getAllBalancesByGroup_success() {
                String groupId = "group-123";

                List<ExpenseGroupBalance> balances = Instancio.ofList(ExpenseGroupBalance.class)
                                .size(3)
                                .create();

                when(balanceRepository.findAllByGroup(groupId)).thenReturn(balances);

                GetGroupBalancesDTO result = balanceService.getAllBalancesByGroup(groupId);

                assertNotNull(result);
                assertEquals(groupId, result.groupId());
                assertEquals(3, result.balances().size());

                assertEquals(balances.get(0).creditor().id(), result.balances().get(0).creditor().userId());
                assertEquals(balances.get(0).debtor().id(), result.balances().get(0).debtor().userId());
                assertEquals(balances.get(0).amount(), result.balances().get(0).amount());
        }

        @Test
        @DisplayName("Get all balances by group - Empty list")
        public void getAllBalancesByGroup_emptyList_success() {
                String groupId = "group-empty";
                when(balanceRepository.findAllByGroup(groupId)).thenReturn(List.of());

                GetGroupBalancesDTO result = balanceService.getAllBalancesByGroup(groupId);

                assertNotNull(result);
                assertEquals(0, result.balances().size());
        }
}