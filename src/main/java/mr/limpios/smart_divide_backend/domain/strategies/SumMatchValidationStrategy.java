package mr.limpios.smart_divide_backend.domain.strategies;

import mr.limpios.smart_divide_backend.domain.dto.ExpenseDebtorDTO;
import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;
import mr.limpios.smart_divide_backend.domain.dto.ExpenseInputDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.DEBTORS_AMOUNT_MISMATCH;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.PAYERS_AMOUNT_MISMATCH;

@Component
public class SumMatchValidationStrategy implements ExpenseDivisionValidationStrategy {

    @Override
    public ParticipantsPayersOfExpenses validate(ExpenseInputDTO dto) {
        BigDecimal totalExpenseAmount = BigDecimal.valueOf(dto.amount());
        List<ValidatedMembers> participants = validateParticipants(dto,  totalExpenseAmount);
        List<ValidatedMembers> payers = validatePayers(dto,  totalExpenseAmount);
        return new ParticipantsPayersOfExpenses(participants, payers);
    }

    private List<ValidatedMembers> validateParticipants(ExpenseInputDTO dto, BigDecimal amount) {
        BigDecimal totalDivision = calculateTotalContribution(amount, dto.participants());

        if (totalDivision.compareTo(amount) != 0) {
            throw new InvalidDataException(DEBTORS_AMOUNT_MISMATCH);
        }

         return dto.participants().stream()
                .map(b -> new ValidatedMembers(
                        b.debtorId(),
                        BigDecimal.valueOf(b.amount())
                )).toList();
    }

    private List<ValidatedMembers> validatePayers(ExpenseInputDTO dto, BigDecimal amount) {
        BigDecimal totalPaid = calculateTotalContribution(amount, dto.payers());

        if (totalPaid.compareTo(amount) != 0) {
            throw new InvalidDataException(PAYERS_AMOUNT_MISMATCH);
        }

        return dto.payers().stream()
                .map(b -> new ValidatedMembers(
                        b.debtorId(),
                        BigDecimal.valueOf(b.amount())
                )).toList();
    }

    private BigDecimal calculateTotalContribution(BigDecimal amount, List<ExpenseDebtorDTO> membersList) {
        return membersList.stream()
                .map(p -> BigDecimal.valueOf(p.amount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
