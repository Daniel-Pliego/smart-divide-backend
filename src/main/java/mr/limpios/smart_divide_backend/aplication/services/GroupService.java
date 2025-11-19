package mr.limpios.smart_divide_backend.aplication.services;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import mr.limpios.smart_divide_backend.aplication.repositories.FriendshipRepository;
import mr.limpios.smart_divide_backend.infraestructure.dto.GroupResumeDTO;
import mr.limpios.smart_divide_backend.infraestructure.dto.GroupDataDTO;
import mr.limpios.smart_divide_backend.infraestructure.dto.UpdateGroupResumeDTO;
import mr.limpios.smart_divide_backend.infraestructure.dto.NewMemberDTO;
import mr.limpios.smart_divide_backend.infraestructure.dto.AddMemberDTO;
import mr.limpios.smart_divide_backend.infraestructure.dto.GroupTransactionHistoryDTO;
import mr.limpios.smart_divide_backend.infraestructure.dto.UserBalanceDTO;
import mr.limpios.smart_divide_backend.infraestructure.dto.ExpenseDetailDTO;
import mr.limpios.smart_divide_backend.infraestructure.dto.PaymentDetailDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import mr.limpios.smart_divide_backend.aplication.repositories.GroupRepository;
import mr.limpios.smart_divide_backend.aplication.repositories.UserRepository;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceNotFoundException;
import mr.limpios.smart_divide_backend.domain.models.Group;
import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.domain.validators.GroupValidator;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.FRIENDSHIP_NOT_FOUND;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.GROUPS_NOT_FOUND_FOR_USER;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.USER_NOT_FOUND;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.GROUP_NOT_FOUND;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.USER_NOT_MEMBER_OF_GROUP;

@Service
public class GroupService {

  private final GroupRepository groupRepository;
  private final UserRepository userRepository;
  private final FriendshipRepository friendshipRepository;
  @Lazy
  private final PaymentService paymentService;
  @Lazy
  private final ExpenseService expenseService;

  public GroupService(
          GroupRepository groupRepository,
          UserRepository userRepository,
          FriendshipRepository friendshipRepository,
          PaymentService paymentService,
          ExpenseService expenseService
  ) {
    this.groupRepository = groupRepository;
    this.userRepository = userRepository;
    this.friendshipRepository = friendshipRepository;
    this.paymentService = paymentService;
    this.expenseService = expenseService;
  }

  public GroupResumeDTO createGroup(GroupDataDTO group, String ownerId) {
    User owner = this.userRepository.getUserbyId(ownerId);

    if (Objects.isNull(owner)) {
      throw new ResourceNotFoundException(USER_NOT_FOUND);
    }

    GroupValidator.validate(group);

    Group savedGroup = this.groupRepository.saveGroup(
        new Group(
            null,
            group.name(),
            group.description(),
            owner,
            "",
            List.of(owner)));

    return new GroupResumeDTO(
        savedGroup.id(),
        savedGroup.name(),
        savedGroup.description(),
        savedGroup.owner().id(),
        0,
        0);
  }

  public UpdateGroupResumeDTO updateGroup(GroupDataDTO group, String groupId) {
    GroupValidator.validate(group);

    Group findedGroup = this.groupRepository.getGroupById(groupId);

    if (Objects.isNull(findedGroup)) {
      throw new ResourceNotFoundException(GROUP_NOT_FOUND);
    }

    Group updatedGroup = this.groupRepository.updateGroupById(
        groupId,
        new Group(
            findedGroup.id(),
            group.name(),
            group.description(),
            findedGroup.owner(),
            findedGroup.type(),
            findedGroup.members()));

    return new UpdateGroupResumeDTO(
        updatedGroup.id(),
        updatedGroup.name(),
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

    return new NewMemberDTO(
        updatedGroup.id(),
        memberToAdd.id(),
        memberToAdd.name(),
        memberToAdd.lastName(),
        memberToAdd.photoUrl());

  }

  public List<GroupDataDTO> getUserGroups(String userId) {
    Set<Group> groups = groupRepository.getGroupsByUserId(userId);
    if (Objects.isNull(groups)) {
      throw new ResourceNotFoundException(GROUPS_NOT_FOUND_FOR_USER);
    }
    return groups.stream()
        .map(group -> new GroupDataDTO(
            group.name(),
            group.description()))
        .collect(Collectors.toList());
  }

  public GroupTransactionHistoryDTO getGroupTransactionHistory(String groupId, String userId) {
    Group group = this.groupRepository.getGroupById(groupId);

    if (Objects.isNull(group)) {
      throw new ResourceNotFoundException(GROUP_NOT_FOUND);
    }

    boolean isMember = group.members().stream()
            .anyMatch(member -> member.id().equals(userId));

    if (!isMember) {
      throw new ResourceNotFoundException(USER_NOT_MEMBER_OF_GROUP);
    }

    List<UserBalanceDTO> userBalances = expenseService.getUserBalancesByGroup(groupId,userId);
    List<ExpenseDetailDTO> expenses = expenseService.getExpensesByGroup(groupId,userBalances);
    List<PaymentDetailDTO> payments = paymentService.getPaymentsByGroup(groupId);

    return new GroupTransactionHistoryDTO(
            group.id(),
            group.name(),
            group.description(),
            group.owner().id(),
            group.type(),
            userBalances,
            payments,
            expenses
    );
  }
}
