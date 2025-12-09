package mr.limpios.smart_divide_backend.application.assemblers;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Predicate;

import mr.limpios.smart_divide_backend.application.dtos.CreateExpenseParticipantDTO;
import mr.limpios.smart_divide_backend.application.dtos.ExpenseInputDTO;
import mr.limpios.smart_divide_backend.application.utils.CollectionUtils;
import mr.limpios.smart_divide_backend.domain.models.*;

public class ExpenseModelAssembler {
  public static List<ExpenseParticipant> createExpenseParticipantsFromValidatedParticipants(
      ExpenseInputDTO addExpenseDTO, Map<String, User> groupMembersMap) {

    Map<String, BigDecimal> payersMap = CollectionUtils.toMap(addExpenseDTO.payers(),
        CreateExpenseParticipantDTO::userId, payer -> BigDecimal.valueOf(payer.amount()));

    Map<String, BigDecimal> participantsMap =
        CollectionUtils.toMap(addExpenseDTO.participants(), CreateExpenseParticipantDTO::userId,
            participant -> BigDecimal.valueOf(participant.amount()));

    Set<String> allMemberIds = new HashSet<>();
    allMemberIds.addAll(payersMap.keySet());
    allMemberIds.addAll(participantsMap.keySet());

    List<ExpenseParticipant> result = new ArrayList<>();
    for (String memberId : allMemberIds) {
      User user = groupMembersMap.get(memberId);
      BigDecimal amountPaid = payersMap.getOrDefault(memberId, BigDecimal.ZERO);
      BigDecimal mustPaid = participantsMap.getOrDefault(memberId, BigDecimal.ZERO);

      result.add(new ExpenseParticipant(null, user, amountPaid, mustPaid));
    }

    return result;
  }

  private static List<String> filterIds(Map<String, BigDecimal> balances,
      Predicate<BigDecimal> predicate) {
    return balances.entrySet().stream().filter(e -> predicate.test(e.getValue()))
        .map(Map.Entry::getKey).toList();
  }

  private static Map<String, BigDecimal> balanceCalculation(Map<String, BigDecimal> payersMap,
      Map<String, BigDecimal> participantsMap) {
    Map<String, BigDecimal> balances = new HashMap<>();
    Set<String> allIds = new HashSet<>();
    allIds.addAll(payersMap.keySet());
    allIds.addAll(participantsMap.keySet());

    for (String id : allIds) {
      BigDecimal paid = payersMap.getOrDefault(id, BigDecimal.ZERO);
      BigDecimal mustPay = participantsMap.getOrDefault(id, BigDecimal.ZERO);
      BigDecimal balance = paid.subtract(mustPay);
      balances.put(id, balance);
    }

    return balances;
  }

  public static List<ExpenseBalance> createExpenseBalanceFromValidatedParticipants(
      ExpenseInputDTO addExpenseDTO, Map<String, User> groupMembersMap) {

    Map<String, BigDecimal> payersMap = CollectionUtils.toMap(addExpenseDTO.payers(),
        CreateExpenseParticipantDTO::userId, payer -> BigDecimal.valueOf(payer.amount()));

    Map<String, BigDecimal> participantsMap =
        CollectionUtils.toMap(addExpenseDTO.participants(), CreateExpenseParticipantDTO::userId,
            participant -> BigDecimal.valueOf(participant.amount()));

    Map<String, BigDecimal> balances = balanceCalculation(payersMap, participantsMap);

    List<String> creditors = filterIds(balances, b -> b.compareTo(BigDecimal.ZERO) > 0);
    List<String> debtors = filterIds(balances, b -> b.compareTo(BigDecimal.ZERO) < 0);

    List<ExpenseBalance> result = new ArrayList<>();
    int iCred = 0;
    int iDebt = 0;

    while (iCred < creditors.size() && iDebt < debtors.size()) {

      String creditor = creditors.get(iCred);
      String debtor = debtors.get(iDebt);

      BigDecimal credBal = balances.get(creditor);
      BigDecimal debtBal = balances.get(debtor).abs();

      BigDecimal amount = credBal.min(debtBal);

      result.add(new ExpenseBalance(null, groupMembersMap.get(creditor),
          groupMembersMap.get(debtor), amount));

      balances.put(creditor, credBal.subtract(amount));
      balances.put(debtor, balances.get(debtor).add(amount));

      if (balances.get(creditor).compareTo(BigDecimal.ZERO) == 0) {
        iCred++;
      }

      if (balances.get(debtor).compareTo(BigDecimal.ZERO) == 0) {
        iDebt++;
      }
    }

    return result;
  }

}
