package mr.limpios.smart_divide_backend.domain.strategies;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.EQUAL_DIVISION_AMOUNT_MISMATCH;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.PAYERS_AMOUNT_MISMATCH;

import org.springframework.stereotype.Component;

import mr.limpios.smart_divide_backend.domain.exceptions.InvalidDataException;
import mr.limpios.smart_divide_backend.domain.dto.ExpenseInputDTO;
import mr.limpios.smart_divide_backend.domain.dto.ExpenseDebtorDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EqualDivisionValidationStrategy implements ExpenseDivisionValidationStrategy {

    @Override
    public ParticipantsPayersOfExpenses validate(ExpenseInputDTO dto) {
        List<ValidatedMembers> participants = validateParticipants(dto);
        List<ValidatedMembers> payers = validatePayers(dto);
        return new ParticipantsPayersOfExpenses(participants, payers);
    }

    private List<ValidatedMembers> validateParticipants(ExpenseInputDTO dto) {
        double equalShare = dto.amount() / dto.participants().size();

        for (ExpenseDebtorDTO participant : dto.participants()) {
            if (Double.compare(participant.amount(), equalShare) != 0) {
                throw new InvalidDataException(
                        EQUAL_DIVISION_AMOUNT_MISMATCH + equalShare);
            }
        }

         return dto.participants().stream()
                .map(b -> new ValidatedMembers(
                        b.debtorId(),
                        BigDecimal.valueOf(b.amount())
                )).toList();
    }

    private List<ValidatedMembers> validatePayers(ExpenseInputDTO dto) {
        BigDecimal totalAmount = BigDecimal.valueOf(dto.amount());
        BigDecimal totalPayers = dto.payers().stream()
                .map(p -> BigDecimal.valueOf(p.amount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

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
}
