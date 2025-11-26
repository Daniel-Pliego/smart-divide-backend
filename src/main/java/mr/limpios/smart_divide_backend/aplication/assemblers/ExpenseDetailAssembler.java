package mr.limpios.smart_divide_backend.aplication.assemblers;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import mr.limpios.smart_divide_backend.domain.dto.ExpenseDetails.DebtorBalanceDTO;
import mr.limpios.smart_divide_backend.domain.dto.ExpenseDetails.ExpenseBalanceDTO;
import mr.limpios.smart_divide_backend.domain.dto.ExpenseDetails.ExpenseDetailDTO;
import mr.limpios.smart_divide_backend.domain.dto.ExpenseDetails.ExpenseParticipantDTO;
import mr.limpios.smart_divide_backend.domain.dto.ExpenseDetails.ExpensePayerDetailDTO;
import mr.limpios.smart_divide_backend.domain.models.Expense;
import mr.limpios.smart_divide_backend.domain.models.ExpenseBalance;
import mr.limpios.smart_divide_backend.domain.models.ExpenseParticipant;
import mr.limpios.smart_divide_backend.domain.models.User;

public class ExpenseDetailAssembler {

  public static ExpenseDetailDTO buildExpenseDetailDTO(Expense expense) {
    List<ExpenseParticipant> participants =
        expense.participants() == null ? List.of() : expense.participants();
    Map<String, ExpenseParticipant> participantsByUser = participants.stream()
        .collect(Collectors.toMap(p -> p.payer().id(), Function.identity(), (a, b) -> a));

    List<ExpenseBalanceDTO> balances =
        groupBalancesByCreditor(expense.balances(), participantsByUser);

    return new ExpenseDetailDTO(expense.id(), expense.type(), expense.description(),
        expense.amount(), expense.createdAt(), expense.evidenceUrl(), balances);
  }

  private static List<ExpenseBalanceDTO> groupBalancesByCreditor(List<ExpenseBalance> balances,
      Map<String, ExpenseParticipant> participantsByUser) {

    return balances.stream().collect(Collectors.groupingBy(ExpenseBalance::creditor)).entrySet()
        .stream().map(e -> buildBalanceForCreditor(e.getKey(), e.getValue(), participantsByUser))
        .toList();
  }

  private static ExpenseBalanceDTO buildBalanceForCreditor(User creditor,
      List<ExpenseBalance> balancesForCreditor,
      Map<String, ExpenseParticipant> participantsByUser) {
    ExpenseParticipant participant = participantsByUser.get(creditor.id());

    ExpensePayerDetailDTO payer = new ExpensePayerDetailDTO(toExpenseParticipantDTO(creditor),
        participant.amountPaid(), participant.amountPaid().subtract(participant.mustPaid()));

    List<DebtorBalanceDTO> debtors = balancesForCreditor.stream()
        .map(b -> new DebtorBalanceDTO(toExpenseParticipantDTO(b.debtor()), b.amountToPaid()))
        .toList();

    return new ExpenseBalanceDTO(payer, debtors);
  }

  private static ExpenseParticipantDTO toExpenseParticipantDTO(User user) {
    return new ExpenseParticipantDTO(user.id(), user.name(), user.lastName(), user.photoUrl());
  }

}
