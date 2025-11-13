package mr.limpios.smart_divide_backend.infraestructure.mappers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import mr.limpios.smart_divide_backend.domain.models.Expense;
import mr.limpios.smart_divide_backend.domain.models.ExpenseBalance;
import mr.limpios.smart_divide_backend.domain.models.ExpenseParticipant;
import mr.limpios.smart_divide_backend.domain.models.Group;
import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.domain.strategies.CalculatedBalance;
import mr.limpios.smart_divide_backend.infraestructure.dto.ExpenseInputDTO;
import mr.limpios.smart_divide_backend.infraestructure.dto.ExpenseResumeDTO;
import mr.limpios.smart_divide_backend.infraestructure.schemas.ExpenseSchema;

public class ExpenseMapper {

        public static ExpenseSchema toSchema(Expense expense) {
                ExpenseSchema expenseSchema = new ExpenseSchema();
                expenseSchema.setId(expense.id());
                expenseSchema.setType(expense.type());
                expenseSchema.setDescription(expense.description());
                expenseSchema.setAmount(expense.amount());
                expenseSchema.setEvidenceUrl(expense.evidenUrl());
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
                return new Expense(
                                expenseSchema.getId(),
                                expenseSchema.getType(),
                                expenseSchema.getDescription(),
                                expenseSchema.getAmount(),
                                expenseSchema.getEvidenceUrl(),
                                expenseSchema.getCreatedAt(),
                                expenseSchema.getDivisionType(),
                                GroupMapper.toModel(expenseSchema.getGroup()),
                                ExpenseParticipantMapper.toModelList(expenseSchema.getParticipants()),
                                ExpenseBalanceMapper.toModelList(expenseSchema.getBalances()));
        }

        public static Expense toEntity(ExpenseInputDTO dto, Group group, List<ExpenseParticipant> participants,
                        List<ExpenseBalance> balances) {
                return new Expense(
                                null,
                                dto.type(),
                                dto.description(),
                                BigDecimal.valueOf(dto.amount()),
                                dto.evidenUrl(),
                                null,
                                dto.divisionType(),
                                group,
                                participants,
                                balances);
        }

        public static ExpenseResumeDTO toResumeDTO(Expense expense) {
                return new ExpenseResumeDTO(
                                expense.id(),
                                expense.type(),
                                expense.description(),
                                expense.amount(),
                                expense.createdAt());
        }

        public static List<ExpenseParticipant> createParticipantsFromBalances(
                        List<CalculatedBalance> balances,
                        Map<String, User> groupMembersMap) {

                return balances.stream()
                                .map(balance -> new ExpenseParticipant(
                                                null,
                                                groupMembersMap.get(balance.debtorId()),
                                                BigDecimal.valueOf(0),
                                                balance.amountToPaid()))
                                .collect(Collectors.toList());
        }

        public static List<ExpenseBalance> createExpenseBalancesFromBalances(
                        List<CalculatedBalance> balances,
                        Map<String, User> groupMembersMap,
                        String creditorId) {
                return balances.stream()
                                .map(balance -> new ExpenseBalance(
                                                null,
                                                groupMembersMap.get(creditorId),
                                                groupMembersMap.get(balance.debtorId()),
                                                balance.amountToPaid()))
                                .collect(Collectors.toList());
        }

}
