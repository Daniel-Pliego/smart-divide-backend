package mr.limpios.smart_divide_backend.infraestructure.mappers;

import java.util.List;
import java.util.stream.Collectors;

import mr.limpios.smart_divide_backend.domain.models.ExpenseParticipant;
import mr.limpios.smart_divide_backend.infraestructure.schemas.ExpenseParticipantSchema;
import mr.limpios.smart_divide_backend.infraestructure.schemas.ExpenseSchema;

public class ExpenseParticipantMapper {

        public static ExpenseParticipantSchema toSchema(
                ExpenseParticipant participant, ExpenseSchema expenseSchema) {
                ExpenseParticipantSchema participantSchema = new ExpenseParticipantSchema();
                participantSchema.setId(participant.id());
                participantSchema.setPayer(UserMapper.toSchema(participant.payer()));
                participantSchema.setAmountPaid(participant.amountPaid());
                participantSchema.setMustPaid(participant.mustPaid());
                participantSchema.setExpense(expenseSchema);

                return participantSchema;
        }

        public static ExpenseParticipant toModel(
                        ExpenseParticipantSchema participantSchema) {
                return new ExpenseParticipant(
                                participantSchema.getId(),
                                UserMapper.toModel(participantSchema.getPayer()),
                                participantSchema.getAmountPaid(),
                                participantSchema.getMustPaid());
        }

        public static List<ExpenseParticipantSchema> toSchemaList(
                        List<ExpenseParticipant> participants, ExpenseSchema expenseSchema) {
                return participants.stream()
                                .map(participant -> toSchema(participant, expenseSchema))
                                .collect(Collectors.toList());
        }

        public static List<ExpenseParticipant> toModelList(
                        List<ExpenseParticipantSchema> participantSchemas) {
                return participantSchemas.stream()
                                .map(ExpenseParticipantMapper::toModel)
                                .collect(Collectors.toList());
        }

}
