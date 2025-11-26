package mr.limpios.smart_divide_backend.aplication.assemblers;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import mr.limpios.smart_divide_backend.domain.dto.ExpenseDetails.DebtorBalanceDTO;
import mr.limpios.smart_divide_backend.domain.dto.ExpenseDetails.ExpenseBalanceDTO;
import mr.limpios.smart_divide_backend.domain.dto.ExpenseDetails.ExpenseDetailDTO;
import mr.limpios.smart_divide_backend.domain.dto.ExpenseDetails.ExpenseParticipantDTO;
import mr.limpios.smart_divide_backend.domain.dto.ExpenseDetails.ExpensePayerDetail;
import mr.limpios.smart_divide_backend.domain.models.Expense;
import mr.limpios.smart_divide_backend.domain.models.ExpenseBalance;
import mr.limpios.smart_divide_backend.domain.models.ExpenseParticipant;
import mr.limpios.smart_divide_backend.domain.models.User;

public class ExpenseDetailAssembler {

  public static ExpenseDetailDTO buildExpenseDetailDTO(Expense expense) {
    List<ExpensePayerDetail> payers = buildPayersDetails(expense.participants());
    List<ExpenseBalanceDTO> balances = buildBalancesDetails(expense.balances());

    return new ExpenseDetailDTO(expense.id(), expense.type(), expense.description(),
        expense.amount(), expense.createdAt(), expense.evidenceUrl(), payers, balances);
  }

  private static List<ExpensePayerDetail> buildPayersDetails(
      List<ExpenseParticipant> participants) {
    return participants.stream().filter(p -> p.amountPaid().compareTo(BigDecimal.ZERO) > 0)
        .map(p -> new ExpensePayerDetail(toExpenseParticipantDTO(p.payer()), p.amountPaid(),
            p.amountPaid().subtract(p.mustPaid())))
        .toList();
  }

  private static List<ExpenseBalanceDTO> buildBalancesDetails(List<ExpenseBalance> balances) {
    return balances.stream().collect(Collectors.groupingBy(ExpenseBalance::creditor)).entrySet()
        .stream().map(entry -> {
          User creditor = entry.getKey();
          List<DebtorBalanceDTO> debtors = entry.getValue().stream()
              .map(balance -> new DebtorBalanceDTO(toExpenseParticipantDTO(balance.debtor()),
                  balance.amountToPaid()))
              .toList();

          return new ExpenseBalanceDTO(toExpenseParticipantDTO(creditor), debtors);
        }).toList();
  }

  private static ExpenseParticipantDTO toExpenseParticipantDTO(User user) {
    return new ExpenseParticipantDTO(user.id(), user.name(), user.lastName(), user.photoUrl());
  }

}
