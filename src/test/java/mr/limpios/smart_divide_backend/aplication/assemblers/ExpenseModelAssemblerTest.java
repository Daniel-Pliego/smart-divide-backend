package mr.limpios.smart_divide_backend.aplication.assemblers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import mr.limpios.smart_divide_backend.aplication.utils.CollectionUtils;
import mr.limpios.smart_divide_backend.domain.dto.ExpenseInputDTO;
import mr.limpios.smart_divide_backend.domain.models.ExpenseBalance;
import mr.limpios.smart_divide_backend.domain.models.ExpenseParticipant;
import mr.limpios.smart_divide_backend.domain.models.User;

@ExtendWith(MockitoExtension.class)
class ExpenseModelAssemblerTest {

    @Test
    void createExpenseParticipantsFromValidatedParticipants_mapsCorrectly() {
        try (MockedStatic<CollectionUtils> utilsMock = Mockito.mockStatic(CollectionUtils.class)) {
            ExpenseInputDTO inputDTO = Instancio.create(ExpenseInputDTO.class);
            
            User user1 = Instancio.create(User.class);
            User user2 = Instancio.create(User.class);
            Map<String, User> membersMap = Map.of("u1", user1, "u2", user2);

            Map<String, BigDecimal> payersMap = Map.of("u1", new BigDecimal("100.00"));
            Map<String, BigDecimal> participantsMap = Map.of("u1", new BigDecimal("50.00"), "u2", new BigDecimal("50.00"));

            utilsMock.when(() -> CollectionUtils.toMap(eq(inputDTO.payers()), any(), any()))
                .thenReturn(payersMap);
            utilsMock.when(() -> CollectionUtils.toMap(eq(inputDTO.participants()), any(), any()))
                .thenReturn(participantsMap);

            List<ExpenseParticipant> result = ExpenseModelAssembler.createExpenseParticipantsFromValidatedParticipants(inputDTO, membersMap);

            assertNotNull(result);
            assertEquals(2, result.size());

            ExpenseParticipant p1 = result.stream().filter(p -> p.payer().equals(user1)).findFirst().orElseThrow();
            assertEquals(new BigDecimal("100.00"), p1.amountPaid());
            assertEquals(new BigDecimal("50.00"), p1.mustPaid());

            ExpenseParticipant p2 = result.stream().filter(p -> p.payer().equals(user2)).findFirst().orElseThrow();
            assertEquals(BigDecimal.ZERO, p2.amountPaid()); // No estaba en payersMap
            assertEquals(new BigDecimal("50.00"), p2.mustPaid());
        }
    }

    @Test
    void createExpenseBalance_simpleSplit_oneCreditorOneDebtor() {
        try (MockedStatic<CollectionUtils> utilsMock = Mockito.mockStatic(CollectionUtils.class)) {
            ExpenseInputDTO inputDTO = Instancio.create(ExpenseInputDTO.class);
            User user1 = Instancio.create(User.class); // Paga todo
            User user2 = Instancio.create(User.class); // No paga nada
            Map<String, User> membersMap = Map.of("u1", user1, "u2", user2);

            Map<String, BigDecimal> payersMap = Map.of("u1", new BigDecimal("100"));
            Map<String, BigDecimal> participantsMap = Map.of("u1", new BigDecimal("50"), "u2", new BigDecimal("50"));

            utilsMock.when(() -> CollectionUtils.toMap(eq(inputDTO.payers()), any(), any())).thenReturn(payersMap);
            utilsMock.when(() -> CollectionUtils.toMap(eq(inputDTO.participants()), any(), any())).thenReturn(participantsMap);

            List<ExpenseBalance> result = ExpenseModelAssembler.createExpenseBalanceFromValidatedParticipants(inputDTO, membersMap);

            assertEquals(1, result.size());
            ExpenseBalance balance = result.get(0);
            
            assertEquals(user1, balance.creditor()); // u1 recibe
            assertEquals(user2, balance.debtor());   // u2 paga
            assertEquals(0, new BigDecimal("50").compareTo(balance.amountToPaid()));
        }
    }

    @Test
    void createExpenseBalance_complexSplit_oneCreditorMultipleDebtors() {
        try (MockedStatic<CollectionUtils> utilsMock = Mockito.mockStatic(CollectionUtils.class)) {
            ExpenseInputDTO inputDTO = Instancio.create(ExpenseInputDTO.class);
            User user1 = Instancio.create(User.class);
            User user2 = Instancio.create(User.class);
            User user3 = Instancio.create(User.class);
            Map<String, User> membersMap = Map.of("u1", user1, "u2", user2, "u3", user3);

            Map<String, BigDecimal> payersMap = Map.of("u1", new BigDecimal("120"));
            Map<String, BigDecimal> participantsMap = Map.of(
                "u1", new BigDecimal("40"),
                "u2", new BigDecimal("40"),
                "u3", new BigDecimal("40")
            );

            utilsMock.when(() -> CollectionUtils.toMap(eq(inputDTO.payers()), any(), any())).thenReturn(payersMap);
            utilsMock.when(() -> CollectionUtils.toMap(eq(inputDTO.participants()), any(), any())).thenReturn(participantsMap);

            List<ExpenseBalance> result = ExpenseModelAssembler.createExpenseBalanceFromValidatedParticipants(inputDTO, membersMap);

            assertEquals(2, result.size());
            
            assertTrue(result.stream().allMatch(b -> b.creditor().equals(user1)));
            
            assertTrue(result.stream().anyMatch(b -> b.debtor().equals(user2) && b.amountToPaid().compareTo(new BigDecimal("40")) == 0));
            assertTrue(result.stream().anyMatch(b -> b.debtor().equals(user3) && b.amountToPaid().compareTo(new BigDecimal("40")) == 0));
        }
    }

    @Test
    void createExpenseBalance_complexSplit_multipleCreditorsOneDebtor() {
        try (MockedStatic<CollectionUtils> utilsMock = Mockito.mockStatic(CollectionUtils.class)) {
            ExpenseInputDTO inputDTO = Instancio.create(ExpenseInputDTO.class);
            User user1 = Instancio.create(User.class);
            User user2 = Instancio.create(User.class);
            User user3 = Instancio.create(User.class);
            Map<String, User> membersMap = Map.of("u1", user1, "u2", user2, "u3", user3);

            Map<String, BigDecimal> payersMap = Map.of("u1", new BigDecimal("45"), "u2", new BigDecimal("45"));
            Map<String, BigDecimal> participantsMap = Map.of("u3", new BigDecimal("90"));

            utilsMock.when(() -> CollectionUtils.toMap(eq(inputDTO.payers()), any(), any())).thenReturn(payersMap);
            utilsMock.when(() -> CollectionUtils.toMap(eq(inputDTO.participants()), any(), any())).thenReturn(participantsMap);

            List<ExpenseBalance> result = ExpenseModelAssembler.createExpenseBalanceFromValidatedParticipants(inputDTO, membersMap);

            assertEquals(2, result.size());
            
            assertTrue(result.stream().allMatch(b -> b.debtor().equals(user3)));
            
            assertTrue(result.stream().anyMatch(b -> b.creditor().equals(user1) && b.amountToPaid().compareTo(new BigDecimal("45")) == 0));
            assertTrue(result.stream().anyMatch(b -> b.creditor().equals(user2) && b.amountToPaid().compareTo(new BigDecimal("45")) == 0));
        }
    }
}