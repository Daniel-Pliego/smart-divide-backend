package mr.limpios.smart_divide_backend.application.assemblers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import mr.limpios.smart_divide_backend.application.dtos.ExpenseDetails.ExpenseBalanceDTO;
import mr.limpios.smart_divide_backend.application.dtos.ExpenseDetails.ExpenseDetailDTO;
import mr.limpios.smart_divide_backend.application.dtos.ExpenseDetails.ExpenseParticipantDTO;
import mr.limpios.smart_divide_backend.application.dtos.ExpenseDetails.ExpensePayerDetailDTO;
import mr.limpios.smart_divide_backend.application.dtos.ExpenseDetails.ExpenseUserAmountDTO;
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

    List<ExpenseUserAmountDTO> paidBy =
        participants.stream().filter(p -> p.amountPaid().compareTo(BigDecimal.ZERO) > 0)
            .map(p -> new ExpenseUserAmountDTO(toExpenseParticipantDTO(p.payer()), p.amountPaid()))
            .toList();

    List<ExpenseUserAmountDTO> distribution = participants.stream()
        .map(p -> new ExpenseUserAmountDTO(toExpenseParticipantDTO(p.payer()), p.mustPaid()))
        .toList();

    return new ExpenseDetailDTO(expense.id(), expense.type(), expense.description(),
        expense.amount(), expense.createdAt(), expense.evidenceUrl(), paidBy, distribution,
        balances);
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

    List<ExpenseUserAmountDTO> debtors = balancesForCreditor.stream()
        .map(b -> new ExpenseUserAmountDTO(toExpenseParticipantDTO(b.debtor()), b.amountToPaid()))
        .toList();

    return new ExpenseBalanceDTO(payer, debtors);
  }

  private static ExpenseParticipantDTO toExpenseParticipantDTO(User user) {
    return new ExpenseParticipantDTO(user.id(), user.name(), user.lastName(), user.photoUrl());
  }

}
