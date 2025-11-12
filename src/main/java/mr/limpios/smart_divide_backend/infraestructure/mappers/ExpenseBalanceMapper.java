package mr.limpios.smart_divide_backend.infraestructure.mappers;

import java.util.List;
import java.util.stream.Collectors;

import mr.limpios.smart_divide_backend.domain.models.ExpenseBalance;
import mr.limpios.smart_divide_backend.infraestructure.schemas.ExpenseBalanceSchema;
import mr.limpios.smart_divide_backend.infraestructure.schemas.ExpenseSchema;

public class ExpenseBalanceMapper {

        public static ExpenseBalanceSchema toSchema(
                ExpenseBalance balance, ExpenseSchema expenseSchema) {
                ExpenseBalanceSchema balanceSchema = new ExpenseBalanceSchema();
                balanceSchema.setId(balance.id());
                balanceSchema.setCreditor(UserMapper.toSchema(balance.creditor()));
                balanceSchema.setDebtor(UserMapper.toSchema(balance.debtor()));
                balanceSchema.setAmountToPaid(balance.amountToPaid());
                balanceSchema.setExpense(expenseSchema);

                return balanceSchema;
        }

        public static ExpenseBalance toModel(
                        ExpenseBalanceSchema balanceSchema) {
                return new ExpenseBalance(
                                balanceSchema.getId(),
                                UserMapper.toModel(balanceSchema.getCreditor()),
                                UserMapper.toModel(balanceSchema.getDebtor()),
                                balanceSchema.getAmountToPaid());
        }

        public static List<ExpenseBalanceSchema> toSchemaList(
                        List<ExpenseBalance> balances, ExpenseSchema expenseSchema) {
                return balances.stream()
                                .map(balance -> toSchema(balance, expenseSchema))
                                .collect(Collectors.toList());
        }

        public static List<ExpenseBalance> toModelList(
                        List<ExpenseBalanceSchema> balanceSchemas) {
                return balanceSchemas.stream()
                                .map(ExpenseBalanceMapper::toModel)
                                .collect(Collectors.toList());
        }
}
