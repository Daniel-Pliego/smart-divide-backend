package mr.limpios.smart_divide_backend.application.services;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.FRIENDSHIP_NOT_FOUND;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.GROUPS_NOT_FOUND_FOR_USER;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.GROUP_NOT_FOUND;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.USER_NOT_FOUND;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.USER_NOT_MEMBER_OF_GROUP;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import mr.limpios.smart_divide_backend.application.dtos.*;
import mr.limpios.smart_divide_backend.application.repositories.ExpenseGroupBalanceRepository;
import mr.limpios.smart_divide_backend.application.repositories.FriendshipRepository;
import mr.limpios.smart_divide_backend.application.repositories.GroupRepository;
import mr.limpios.smart_divide_backend.application.repositories.UserRepository;
import mr.limpios.smart_divide_backend.application.validators.GroupValidator;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceNotFoundException;
import mr.limpios.smart_divide_backend.domain.models.Group;
import mr.limpios.smart_divide_backend.domain.models.User;

@Service
public class GroupService {

  private final GroupRepository groupRepository;
  private final UserRepository userRepository;
  private final FriendshipRepository friendshipRepository;
  private final ExpenseGroupBalanceRepository expenseGroupBalanceRepository;
  @Lazy
  private final PaymentService paymentService;
  @Lazy
  private final ExpenseService expenseService;
  private final NotificationService notificationService;

  public GroupService(GroupRepository groupRepository, UserRepository userRepository,
      FriendshipRepository friendshipRepository,
      ExpenseGroupBalanceRepository expenseGroupBalanceRepository, PaymentService paymentService,
      ExpenseService expenseService, NotificationService notificationService) {
    this.groupRepository = groupRepository;
    this.userRepository = userRepository;
    this.friendshipRepository = friendshipRepository;
    this.paymentService = paymentService;
    this.expenseGroupBalanceRepository = expenseGroupBalanceRepository;
    this.expenseService = expenseService;
    this.notificationService = notificationService;
  }

  public GroupResumeDTO createGroup(CreateGroupDTO group, String ownerId) {
    User owner = this.userRepository.getUserbyId(ownerId);

    if (Objects.isNull(owner)) {
      throw new ResourceNotFoundException(USER_NOT_FOUND);
    }

    GroupValidator.validate(group);

    Group savedGroup = this.groupRepository.saveGroup(
        new Group(null, group.name(), group.description(), owner, group.type(), List.of(owner)));

    return new GroupResumeDTO(savedGroup.id(), savedGroup.name(), savedGroup.type(),
        new BigDecimal(0), new BigDecimal(0));
  }

  public UpdateGroupResumeDTO updateGroup(CreateGroupDTO group, String groupId) {
    GroupValidator.validate(group);

    Group findedGroup = this.groupRepository.getGroupById(groupId);

    if (Objects.isNull(findedGroup)) {
      throw new ResourceNotFoundException(GROUP_NOT_FOUND);
    }

    Group updatedGroup =
        this.groupRepository.updateGroupById(groupId, new Group(findedGroup.id(), group.name(),
            group.description(), findedGroup.owner(), findedGroup.type(), findedGroup.members()));

    return new UpdateGroupResumeDTO(updatedGroup.id(), updatedGroup.name(),
        updatedGroup.description());
  }

  public NewMemberDTO addMemberToGroup(AddMemberDTO addMemberDTO, String groupId, String ownerId) {
    User owner = this.userRepository.getUserbyId(ownerId);
    User memberToAdd = this.userRepository.getUserbyId(addMemberDTO.memberId());

    if (Objects.isNull(owner) || Objects.isNull(memberToAdd)) {
      throw new ResourceNotFoundException(USER_NOT_FOUND);
    }

    Group group = groupRepository.getGroupById(groupId);

    if (Objects.isNull(group)) {
      throw new ResourceNotFoundException(GROUP_NOT_FOUND);
    }

    Boolean isFriend = this.friendshipRepository.areFriends(ownerId, memberToAdd.id());

    if (!isFriend) {
      throw new ResourceNotFoundException(FRIENDSHIP_NOT_FOUND);
    }

    Group updatedGroup = this.groupRepository.addMemberToGroup(groupId, memberToAdd.id());
    notificationService.notifyMemberAdded(owner, group, memberToAdd);

    return new NewMemberDTO(updatedGroup.id(), memberToAdd.id(), memberToAdd.name(),
        memberToAdd.lastName(), memberToAdd.photoUrl());

  }

  public List<GroupResumeDTO> getUserGroups(String userId) {

    Set<Group> groups = groupRepository.getGroupsByUserId(userId);

    if (groups == null || groups.isEmpty()) {
      throw new ResourceNotFoundException(GROUPS_NOT_FOUND_FOR_USER);
    }

    return groups.stream().map(group -> {

      BigDecimal totalCredits =
          expenseGroupBalanceRepository.getTotalCreditsByGroupAndDebtor(group.id(), userId);
      BigDecimal totalDebts =
          expenseGroupBalanceRepository.getTotalDebtsByGroupAndDebtor(group.id(), userId);

      return new GroupResumeDTO(group.id(), group.name(), group.type(), totalDebts, totalCredits);

    }).collect(Collectors.toList());
  }

  public GroupTransactionHistoryDTO getGroupTransactionHistory(String groupId, String userId) {
    Group group = this.groupRepository.getGroupById(groupId);

    if (Objects.isNull(group)) {
      throw new ResourceNotFoundException(GROUP_NOT_FOUND);
    }

    boolean isMember = group.members().stream().anyMatch(member -> member.id().equals(userId));

    if (!isMember) {
      throw new ResourceNotFoundException(USER_NOT_MEMBER_OF_GROUP);
    }

    List<UserBalanceDTO> userBalances = expenseService.getUserBalancesByGroup(groupId, userId);
    List<ExpenseSummaryDTO> expenses =
        expenseService.getExpensesByGroup(groupId, userBalances, userId);
    List<PaymentDetailDTO> payments = paymentService.getPaymentsByGroup(groupId);

    return new GroupTransactionHistoryDTO(group.id(), group.name(), group.description(),
        group.owner().id(), group.type(), userBalances, payments, expenses);
  }

  public List<MemberResumeDTO> getGroupMembers(String groupId) {
    Group group = groupRepository.getGroupById(groupId);

    if (Objects.isNull(group)) {
      throw new ResourceNotFoundException(GROUP_NOT_FOUND);
    }

    return group.members().stream().map(member -> new MemberResumeDTO(member.id(), member.name(),
        member.lastName(), member.photoUrl())).collect(Collectors.toList());

  }
}
