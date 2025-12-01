package mr.limpios.smart_divide_backend.aplication.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import mr.limpios.smart_divide_backend.aplication.repositories.ExpenseGroupBalanceRepository;
import mr.limpios.smart_divide_backend.domain.dto.BalanceDetailDTO;
import mr.limpios.smart_divide_backend.domain.dto.GetGroupBalancesDTO;
import mr.limpios.smart_divide_backend.domain.models.ExpenseGroupBalance;
import mr.limpios.smart_divide_backend.domain.models.Group;
import mr.limpios.smart_divide_backend.domain.models.User;

@Service
@AllArgsConstructor
public class ExpenseGroupBalanceService {

  private final ExpenseGroupBalanceRepository balanceRepository;

  @Transactional
  public void normalize(User firstUser, User secondUser, Group group) {

    String groupId = group.id();
    String firstUserId = firstUser.id();
    String secondUserId = secondUser.id();

    var balanceUserTwoOwesOneOpt = balanceRepository.findByCreditorAndDebtorAndGroup(firstUserId, secondUserId,
        groupId);
    var balanceUserOneOwesTwoOpt = balanceRepository.findByCreditorAndDebtorAndGroup(secondUserId, firstUserId,
        groupId);

    BigDecimal amountUserTwoOwesOne = balanceUserTwoOwesOneOpt.map(ExpenseGroupBalance::amount).orElse(BigDecimal.ZERO);

    BigDecimal amountUserOneOwesTwo = balanceUserOneOwesTwoOpt.map(ExpenseGroupBalance::amount).orElse(BigDecimal.ZERO);

    BigDecimal netBalanceFirstUser = amountUserTwoOwesOne.subtract(amountUserOneOwesTwo);

    balanceUserTwoOwesOneOpt
        .ifPresent(balance -> balanceRepository.deleteExpenseGroupBalance(balance.id()));
    balanceUserOneOwesTwoOpt
        .ifPresent(balance -> balanceRepository.deleteExpenseGroupBalance(balance.id()));

    if (netBalanceFirstUser.compareTo(BigDecimal.ZERO) == 0)
      return;

    ExpenseGroupBalance simplifiedBalance;

    if (netBalanceFirstUser.compareTo(BigDecimal.ZERO) > 0) {
      simplifiedBalance = new ExpenseGroupBalance(null, firstUser, secondUser, netBalanceFirstUser, group);
    } else {
      simplifiedBalance = new ExpenseGroupBalance(null, secondUser, firstUser, netBalanceFirstUser.abs(), group);
    }
    balanceRepository.saveExpenseGroupBalance(simplifiedBalance);
  }

  public GetGroupBalancesDTO getAllBalancesByGroup(String groupId) {
    List<ExpenseGroupBalance> balances = balanceRepository.findAllByGroup(groupId);
    List<BalanceDetailDTO> balanceDetails = balances.stream()
        .map(balance -> new BalanceDetailDTO(
            balance.creditor().id(),
            balance.debtor().id(),
            balance.amount()))
        .toList();
    return new GetGroupBalancesDTO(groupId, balanceDetails);
  }

   @Transactional
   public void applyReverseBalance(User originalCreditor, User originalDebtor, BigDecimal amountToReverse, Group group) {

       String groupId = group.id();
       var forwardBalanceOpt = balanceRepository.findByCreditorAndDebtorAndGroup(originalCreditor.id(), originalDebtor.id(), groupId);
       var reverseBalanceOpt = balanceRepository.findByCreditorAndDebtorAndGroup(originalDebtor.id(), originalCreditor.id(), groupId);
       BigDecimal positiveAmountToReverse = amountToReverse.abs();

       if (forwardBalanceOpt.isPresent()) {
           ExpenseGroupBalance currentBalance = forwardBalanceOpt.get();
           BigDecimal newAmount = currentBalance.amount().subtract(positiveAmountToReverse);
           saveUpdatedGroupBalance(currentBalance, newAmount);
       } else if (reverseBalanceOpt.isPresent()) {
           ExpenseGroupBalance currentBalance = reverseBalanceOpt.get();
           BigDecimal newAmount = currentBalance.amount().add(positiveAmountToReverse);
           saveUpdatedGroupBalance(currentBalance, newAmount);
       } else {
           ExpenseGroupBalance updatedBalance = new ExpenseGroupBalance(
                   null,
                   originalDebtor,
                   originalCreditor,
                   amountToReverse,
                   group
           );
           balanceRepository.saveExpenseGroupBalance(updatedBalance);
       }
       normalize(originalCreditor, originalDebtor, group);
   }

   private void saveUpdatedGroupBalance(ExpenseGroupBalance currentBalance, BigDecimal newAmount) {
       ExpenseGroupBalance updatedBalance = new ExpenseGroupBalance(
               currentBalance.id(),
               currentBalance.creditor(),
               currentBalance.debtor(),
               newAmount,
               currentBalance.group()
       );
       balanceRepository.saveExpenseGroupBalance(updatedBalance);
   }


}
