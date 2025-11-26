package mr.limpios.smart_divide_backend.aplication.services;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Set;

import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mr.limpios.smart_divide_backend.aplication.repositories.GroupRepository;
import mr.limpios.smart_divide_backend.aplication.repositories.ExpenseGroupBalanceRepository;
import mr.limpios.smart_divide_backend.aplication.repositories.UserRepository;
import mr.limpios.smart_divide_backend.domain.exceptions.ResourceNotFoundException;
import mr.limpios.smart_divide_backend.domain.models.Group;
import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.domain.dto.AddMemberDTO;
import mr.limpios.smart_divide_backend.domain.dto.CreateGroupDTO;
import mr.limpios.smart_divide_backend.domain.dto.ExpenseSummaryDTO;
import mr.limpios.smart_divide_backend.domain.dto.GroupResumeDTO;
import mr.limpios.smart_divide_backend.domain.dto.GroupTransactionHistoryDTO;
import mr.limpios.smart_divide_backend.domain.dto.PaymentDetailDTO;
import mr.limpios.smart_divide_backend.domain.dto.UserBalanceDTO;
import mr.limpios.smart_divide_backend.infraestructure.repositories.impl.FriendShipRepositoryImp;

@ExtendWith(MockitoExtension.class)
public class GroupServiceTest {

        @Mock
        private GroupRepository groupRepository;
        @Mock
        private UserRepository userRepository;
        @Mock
        private FriendShipRepositoryImp friendshipRepository;
        @Mock
        private ExpenseGroupBalanceRepository expenseGroupBalanceRepository;
        @Mock
        private ExpenseService expenseService;
        @Mock
        private PaymentService paymentService;
        @InjectMocks
        private GroupService groupService;

        @Test
        @DisplayName("Get all groups successfully")
        public void getAllGroups_success() {

                Set<Group> groups = Instancio.ofSet(Group.class)
                                .size(3)
                                .create();

                when(groupRepository.getGroupsByUserId("test")).thenReturn(groups);

                List<GroupResumeDTO> result = groupService.getUserGroups("test");

                assertEquals(groups.size(), result.size());
        }

        @Test
        @DisplayName("Get all groups throws ResourceNotFoundException when user has no groups")
        public void getAllGroups_emptyGroups_throwsResourceNotFoundException() {

                when(groupRepository.getGroupsByUserId("test")).thenReturn(null);

                assertThrows(ResourceNotFoundException.class,
                                () -> groupService.getUserGroups("test"));
        }

        @Test
        @DisplayName("Get all groups throws ResourceNotFoundException when user has no groups (empty set)")
        public void getAllGroups_noGroups_throwsResourceNotFoundException() {

                when(groupRepository.getGroupsByUserId("test")).thenReturn(Set.of());

                assertThrows(ResourceNotFoundException.class,
                                () -> groupService.getUserGroups("test"));
        }

        @Test
        @DisplayName("Successful group creation")
        public void createGroup_success() {

                CreateGroupDTO groupDataDTO = Instancio.create(CreateGroupDTO.class);
                Group savedGroup = Instancio.create(Group.class);
                User owner = Instancio.create(User.class);

                when(userRepository.getUserbyId(anyString()))
                                .thenReturn(owner);

                when(groupRepository.saveGroup(ArgumentMatchers.any(Group.class)))
                                .thenReturn(savedGroup);

                var result = groupService.createGroup(groupDataDTO, "owner-id");

                assertEquals(savedGroup.id(), result.id());
                assertEquals(savedGroup.name(), result.name());
        }

        @Test
        @DisplayName("Create group throws ResourceNotFoundException when owner user not found")
        public void createGroup_ownerNotFound_throwsResourceNotFoundException() {

                CreateGroupDTO groupDataDTO = Instancio.create(CreateGroupDTO.class);

                when(userRepository.getUserbyId(anyString()))
                                .thenReturn(null);

                assertThrows(ResourceNotFoundException.class,
                                () -> groupService.createGroup(groupDataDTO, "owner-id"));
        }

        @Test
        @DisplayName("Update group successfully")
        public void updateGroup_success() {
                CreateGroupDTO groupDataDTO = Instancio.create(CreateGroupDTO.class);
                Group existingGroup = Instancio.create(Group.class);
                Group updatedGroup = Instancio.create(Group.class);

                when(groupRepository.getGroupById(anyString()))
                                .thenReturn(existingGroup);

                when(groupRepository.updateGroupById(anyString(), ArgumentMatchers.any(Group.class)))
                                .thenReturn(updatedGroup);

                var result = groupService.updateGroup(groupDataDTO, "test");

                assertEquals(updatedGroup.id(), result.id());
        }

        @Test
        @DisplayName("Update group throws ResourceNotFoundException when group not found")
        public void updateGroup_groupNotFound_throwsResourceNotFoundException() {
                CreateGroupDTO groupDataDTO = Instancio.create(CreateGroupDTO.class);

                when(groupRepository.getGroupById(anyString()))
                                .thenReturn(null);

                assertThrows(ResourceNotFoundException.class,
                                () -> groupService.updateGroup(groupDataDTO, "test"));
        }

        @Test
        @DisplayName("Add member to group successfully")
        public void addMemberToGroup_success() {
                User owner = Instancio.create(User.class);
                User memberToAdd = Instancio.create(User.class);
                Group existingGroup = Instancio.create(Group.class);
                Group newGroup = Instancio.create(Group.class);
                newGroup.members().add(memberToAdd);
                AddMemberDTO addMemberDTO = new AddMemberDTO(memberToAdd.id());

                when(userRepository.getUserbyId(owner.id()))
                                .thenReturn(owner);

                when(userRepository.getUserbyId(memberToAdd.id()))
                                .thenReturn(memberToAdd);

                when(groupRepository.getGroupById(anyString()))
                                .thenReturn(existingGroup);

                when(friendshipRepository.areFriends(owner.id(), memberToAdd.id()))
                                .thenReturn(true);

                when(groupRepository.addMemberToGroup(anyString(), anyString()))
                                .thenReturn(newGroup);

                var result = groupService.addMemberToGroup(addMemberDTO, "group-id", owner.id());

                assertEquals(memberToAdd.id(), result.memberId());
        }

        @Test
        @DisplayName("Add member to group throws ResourceNotFoundException when owner user not found")
        public void addMemberToGroup_ownerNotFound_throwsResourceNotFoundException() {
                AddMemberDTO addMemberDTO = Instancio.create(AddMemberDTO.class);

                when(userRepository.getUserbyId(anyString()))
                                .thenReturn(null);

                assertThrows(ResourceNotFoundException.class,
                                () -> groupService.addMemberToGroup(addMemberDTO, "group-id", "owner-id"));
        }

        @Test
        @DisplayName("Add member to group throws ResourceNotFoundException when member user not found")
        public void addMemberToGroup_memberNotFound_throwsResourceNotFoundException() {
                User owner = Instancio.create(User.class);
                AddMemberDTO addMemberDTO = Instancio.create(AddMemberDTO.class);

                when(userRepository.getUserbyId(owner.id()))
                                .thenReturn(owner);

                when(userRepository.getUserbyId(addMemberDTO.memberId()))
                                .thenReturn(null);

                assertThrows(ResourceNotFoundException.class,
                                () -> groupService.addMemberToGroup(addMemberDTO, "group-id", owner.id()));
        }

        @Test
        @DisplayName("Add member to group throws ResourceNotFoundException when group not found")
        public void addMemberToGroup_groupNotFound_throwsResourceNotFoundException() {
                User owner = Instancio.create(User.class);
                User memberToAdd = Instancio.create(User.class);
                AddMemberDTO addMemberDTO = new AddMemberDTO(memberToAdd.id());

                when(userRepository.getUserbyId(owner.id()))
                                .thenReturn(owner);

                when(userRepository.getUserbyId(memberToAdd.id()))
                                .thenReturn(memberToAdd);

                when(groupRepository.getGroupById(anyString()))
                                .thenReturn(null);

                assertThrows(ResourceNotFoundException.class,
                                () -> groupService.addMemberToGroup(addMemberDTO, "group-id", owner.id()));
        }

        @Test
        @DisplayName("Add member to group throws ResourceNotFoundException when users are not friends")
        public void addMemberToGroup_usersNotFriends_throwsResourceNotFoundException() {
                User owner = Instancio.create(User.class);
                User memberToAdd = Instancio.create(User.class);
                Group existingGroup = Instancio.create(Group.class);
                AddMemberDTO addMemberDTO = new AddMemberDTO(memberToAdd.id());

                when(userRepository.getUserbyId(owner.id()))
                                .thenReturn(owner);

                when(userRepository.getUserbyId(memberToAdd.id()))
                                .thenReturn(memberToAdd);

                when(groupRepository.getGroupById(anyString()))
                                .thenReturn(existingGroup);

                when(friendshipRepository.areFriends(owner.id(), memberToAdd.id()))
                                .thenReturn(false);

                assertThrows(ResourceNotFoundException.class,
                                () -> groupService.addMemberToGroup(addMemberDTO, "group-id", owner.id()));
        }

        @Test
        @DisplayName("Get group transactions balance no group found throws ResourceNotFoundException")
        public void getGroupTransactionsBalance_noGroupFound_throwsResourceNotFoundException() {

                when(groupRepository.getGroupById(anyString()))
                                .thenReturn(null);

                assertThrows(ResourceNotFoundException.class,
                                () -> groupService.getGroupTransactionHistory("group-id", "user-id"));
        }

        @Test
        @DisplayName("Get group transactions balance user not member throws ResourceNotFoundException")
        public void getGroupTransactionsBalance_userNotMember_throwsResourceNotFoundException() {
                Group group = Instancio.create(Group.class);

                when(groupRepository.getGroupById(anyString()))
                                .thenReturn(group);

                assertThrows(ResourceNotFoundException.class,
                                () -> groupService.getGroupTransactionHistory("group-id", "user-id"));
        }

        @Test
        @DisplayName("Get group transactions balance successfully")
        public void getGroupTransactionsBalance_success() {
                Group group = Instancio.create(Group.class);
                String userId = group.members().iterator().next().id();
                List<UserBalanceDTO> balances = Instancio.ofList(UserBalanceDTO.class)
                                .size(3)
                                .create();
                List<ExpenseSummaryDTO> expenses = Instancio.ofList(ExpenseSummaryDTO.class)
                                .size(5)
                                .create();
                List<PaymentDetailDTO> payments = Instancio.ofList(PaymentDetailDTO.class)
                                .size(4)
                                .create();

                GroupTransactionHistoryDTO result = new GroupTransactionHistoryDTO(
                                group.id(), group.name(), group.description(), group.owner().id(), group.type(),
                                balances, payments, expenses);

                when(groupRepository.getGroupById(anyString()))
                                .thenReturn(group);

                when(expenseService.getUserBalancesByGroup(anyString(), anyString())).thenReturn(balances);

                when(expenseService.getExpensesByGroup(anyString(), ArgumentMatchers.anyList(), anyString()))
                                .thenReturn(expenses);

                when(paymentService.getPaymentsByGroup(anyString())).thenReturn(payments);

                assertEquals(result, groupService.getGroupTransactionHistory("group-id", userId));
        }


        @Test
        @DisplayName("Get group members no group found throws ResourceNotFoundException")
        public void getGroupMembers_noGroupFound_throwsResourceNotFoundException() {

                when(groupRepository.getGroupById(anyString()))
                                .thenReturn(null);

                assertThrows(ResourceNotFoundException.class,
                                () -> groupService.getGroupMembers("group-id"));
        }

        @Test
        @DisplayName("Get group members successfully")
        public void getGroupMembers_success() {
                Group group = Instancio.create(Group.class);

                when(groupRepository.getGroupById(anyString()))
                                .thenReturn(group);

                var result = groupService.getGroupMembers("group-id");

                assertEquals(group.members().size(), result.size());
        }

}
