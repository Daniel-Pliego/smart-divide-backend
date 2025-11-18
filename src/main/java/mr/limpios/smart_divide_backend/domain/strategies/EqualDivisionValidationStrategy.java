package mr.limpios.smart_divide_backend.domain.strategies;

import org.springframework.stereotype.Component;

import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;
import mr.limpios.smart_divide_backend.domain.dto.ExpenseInputDTO;
import mr.limpios.smart_divide_backend.domain.dto.ExpenseDebtorDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.*;

@Component
public class EqualDivisionValidationStrategy implements ExpenseDivisionValidationStrategy {

    @Override
    public ParticipantsPayersOfExpenses validate(ExpenseInputDTO dto) {
        List<ValidatedMembers> participants = validateParticipants(dto);
        List<ValidatedMembers> payers = validatePayers(dto);
        return new ParticipantsPayersOfExpenses(participants, payers);
    }

    private List<ValidatedMembers> validateParticipants(ExpenseInputDTO dto) {
        BigDecimal totalAmount = BigDecimal.valueOf(dto.amount());
        BigDecimal totalDebt = getSumOfAmount(dto.participants());
        if (totalDebt.compareTo(totalAmount) != 0) {
            throw new InvalidDataException(
                    DEBTORS_AMOUNT_MISMATCH);
        }

        BigDecimal count = BigDecimal.valueOf(dto.participants().size());
        BigDecimal expectedShare = totalAmount.divide(count, 2, RoundingMode.HALF_UP);
        BigDecimal tolerance = new BigDecimal("0.01");

        for (ExpenseDebtorDTO participant : dto.participants()) {
            BigDecimal actualShare = BigDecimal.valueOf(participant.amount());
            BigDecimal difference = actualShare.subtract(expectedShare).abs();

            if (difference.compareTo(tolerance) > 0) {
                throw new InvalidDataException(
                        EQUAL_DIVISION_AMOUNT_MISMATCH);}
        }

         return dto.participants().stream()
                .map(b -> new ValidatedMembers(
                        b.debtorId(),
                        BigDecimal.valueOf(b.amount())
                )).toList();
    }

    private List<ValidatedMembers> validatePayers(ExpenseInputDTO dto) {
        BigDecimal totalAmount = BigDecimal.valueOf(dto.amount());
        BigDecimal totalPayers = getSumOfAmount(dto.payers());
        if (totalPayers.compareTo(totalAmount) != 0) {
            throw new InvalidDataException(
                    PAYERS_AMOUNT_MISMATCH);
        }

         return dto.payers().stream()
                .map(b -> new ValidatedMembers(
                        b.debtorId(),
                        BigDecimal.valueOf(b.amount())
                )).toList();
    }

    private BigDecimal getSumOfAmount(List<ExpenseDebtorDTO> memberList){
        return memberList.stream()
                .map(p -> BigDecimal.valueOf(p.amount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
