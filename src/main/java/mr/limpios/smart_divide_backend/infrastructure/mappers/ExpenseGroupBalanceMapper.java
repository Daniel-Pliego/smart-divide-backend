package mr.limpios.smart_divide_backend.infrastructure.mappers;

import mr.limpios.smart_divide_backend.domain.models.ExpenseGroupBalance;
import mr.limpios.smart_divide_backend.infrastructure.schemas.ExpenseGroupBalanceSchema;

public class ExpenseGroupBalanceMapper {

  public static ExpenseGroupBalanceSchema toSchema(ExpenseGroupBalance expenseGB) {
    return new ExpenseGroupBalanceSchema(expenseGB.id(), UserMapper.toSchema(expenseGB.creditor()),
        UserMapper.toSchema(expenseGB.debtor()), expenseGB.amount(),
        GroupMapper.toSchema(expenseGB.group()));
  }

  public static ExpenseGroupBalance toModel(ExpenseGroupBalanceSchema expenseGBS) {
    return new ExpenseGroupBalance(expenseGBS.getId(), UserMapper.toModel(expenseGBS.getCreditor()),
        UserMapper.toModel(expenseGBS.getDebtor()), expenseGBS.getAmount(),
        GroupMapper.toModel(expenseGBS.getGroup()));
  }

}
