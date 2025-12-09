package mr.limpios.smart_divide_backend.application.assemblers;

import java.math.BigDecimal;
import java.util.List;

import mr.limpios.smart_divide_backend.application.dtos.*;
import mr.limpios.smart_divide_backend.domain.models.Group;
import mr.limpios.smart_divide_backend.domain.models.User;

public class GroupAssembler {

  public static GroupResumeDTO toGroupResumeDTO(Group group, BigDecimal totalDebts,
      BigDecimal totalCredits) {
    return new GroupResumeDTO(group.id(), group.name(), group.type(), totalDebts, totalCredits);
  }

  public static UpdateGroupResumeDTO toUpdateGroupResumeDTO(Group group) {
    return new UpdateGroupResumeDTO(group.id(), group.name(), group.description());
  }

  public static NewMemberDTO toNewMemberDTO(Group group, User member) {
    return new NewMemberDTO(group.id(), member.id(), member.name(), member.lastName(),
        member.photoUrl());
  }

  public static MemberResumeDTO toMemberResumeDTO(User member) {
    return new MemberResumeDTO(member.id(), member.name(), member.lastName(), member.photoUrl());
  }

  public static GroupTransactionHistoryDTO toGroupTransactionHistoryDTO(Group group,
      List<UserBalanceDTO> userBalances, List<PaymentDetailDTO> payments,
      List<ExpenseSummaryDTO> expenses) {
    return new GroupTransactionHistoryDTO(group.id(), group.name(), group.description(),
        group.owner().id(), group.type(), userBalances, payments, expenses);
  }

}
