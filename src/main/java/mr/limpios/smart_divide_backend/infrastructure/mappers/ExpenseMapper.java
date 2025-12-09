package mr.limpios.smart_divide_backend.infrastructure.mappers;

import mr.limpios.smart_divide_backend.domain.models.Expense;
import mr.limpios.smart_divide_backend.infrastructure.schemas.ExpenseSchema;

public class ExpenseMapper {

  public static ExpenseSchema toSchema(Expense expense) {
    ExpenseSchema expenseSchema = new ExpenseSchema();
    expenseSchema.setId(expense.id());
    expenseSchema.setType(expense.type());
    expenseSchema.setDescription(expense.description());
    expenseSchema.setAmount(expense.amount());
    expenseSchema.setEvidenceUrl(expense.evidenceUrl());
    if (expense.createdAt() != null) {
      expenseSchema.setCreatedAt(expense.createdAt());
    }
    expenseSchema.setDivisionType(expense.divisionType());
    expenseSchema.setGroup(GroupMapper.toSchema(expense.group()));
    expenseSchema.setParticipants(
        ExpenseParticipantMapper.toSchemaList(expense.participants(), expenseSchema));
    expenseSchema.setBalances(ExpenseBalanceMapper.toSchemaList(expense.balances(), expenseSchema));

    return expenseSchema;
  }

  public static Expense toModel(ExpenseSchema expenseSchema) {
    return new Expense(expenseSchema.getId(), expenseSchema.getType(),
        expenseSchema.getDescription(), expenseSchema.getAmount(), expenseSchema.getEvidenceUrl(),
        expenseSchema.getCreatedAt(), expenseSchema.getDivisionType(),
        GroupMapper.toModel(expenseSchema.getGroup()),
        ExpenseParticipantMapper.toModelList(expenseSchema.getParticipants()),
        ExpenseBalanceMapper.toModelList(expenseSchema.getBalances()));
  }
}
