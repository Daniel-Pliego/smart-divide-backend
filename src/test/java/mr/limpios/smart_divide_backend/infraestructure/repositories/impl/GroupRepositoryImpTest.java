package mr.limpios.smart_divide_backend.infraestructure.repositories.impl;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.EXISTING_FRIEND_IN_THE_GROUP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import mr.limpios.smart_divide_backend.domain.exceptions.ResourceExistException;
import mr.limpios.smart_divide_backend.domain.models.Group;
import mr.limpios.smart_divide_backend.infraestructure.mappers.GroupMapper;
import mr.limpios.smart_divide_backend.infraestructure.repositories.jpa.JPAGroupRepository;
import mr.limpios.smart_divide_backend.infraestructure.repositories.jpa.JPAUserRepository;
import mr.limpios.smart_divide_backend.infraestructure.schemas.GroupSchema;
import mr.limpios.smart_divide_backend.infraestructure.schemas.UserSchema;

@ExtendWith(MockitoExtension.class)
class GroupRepositoryImpTest {

    @Mock
    private JPAGroupRepository jpaGroupRepository;

    @Mock
    private JPAUserRepository jpaUserRepository;

    @InjectMocks
    private GroupRepositoryImp groupRepository;

    @Test
    void saveGroup_success() {
        try (MockedStatic<GroupMapper> mapperMock = Mockito.mockStatic(GroupMapper.class)) {
            Group group = Instancio.create(Group.class);
            GroupSchema groupSchema = Instancio.create(GroupSchema.class);
            GroupSchema savedSchema = Instancio.create(GroupSchema.class);

            mapperMock.when(() -> GroupMapper.toSchema(group)).thenReturn(groupSchema);
            when(jpaGroupRepository.save(groupSchema)).thenReturn(savedSchema);
            mapperMock.when(() -> GroupMapper.toModel(savedSchema)).thenReturn(group);

            Group result = groupRepository.saveGroup(group);

            assertNotNull(result);
            verify(jpaGroupRepository).save(groupSchema);
        }
    }

    @Test
    void getGroupById_found() {
        try (MockedStatic<GroupMapper> mapperMock = Mockito.mockStatic(GroupMapper.class)) {
            String groupId = "group-1";
            GroupSchema groupSchema = Instancio.create(GroupSchema.class);
            Group group = Instancio.create(Group.class);

            when(jpaGroupRepository.findById(groupId)).thenReturn(Optional.of(groupSchema));
            mapperMock.when(() -> GroupMapper.toModel(groupSchema)).thenReturn(group);

            Group result = groupRepository.getGroupById(groupId);

            assertNotNull(result);
            assertEquals(group, result);
        }
    }

    @Test
    void getGroupById_notFound() {
        String groupId = "group-1";
        when(jpaGroupRepository.findById(groupId)).thenReturn(Optional.empty());

        Group result = groupRepository.getGroupById(groupId);

        assertNull(result);
    }

    @Test
    void updateGroupById_success() {
        try (MockedStatic<GroupMapper> mapperMock = Mockito.mockStatic(GroupMapper.class)) {
            String groupId = "group-1";
            Group inputGroup = Instancio.create(Group.class);
            GroupSchema existingSchema = Instancio.create(GroupSchema.class);
            GroupSchema savedSchema = Instancio.create(GroupSchema.class);

            when(jpaGroupRepository.getReferenceById(groupId)).thenReturn(existingSchema);
            when(jpaGroupRepository.save(existingSchema)).thenReturn(savedSchema);
            mapperMock.when(() -> GroupMapper.toModel(savedSchema)).thenReturn(inputGroup);

            Group result = groupRepository.updateGroupById(groupId, inputGroup);

            assertNotNull(result);
            verify(jpaGroupRepository).save(existingSchema);
        }
    }

    @Test
    void addMemberToGroup_success() {
        try (MockedStatic<GroupMapper> mapperMock = Mockito.mockStatic(GroupMapper.class)) {
            String groupId = "group-1";
            String memberId = "user-1";
            
            GroupSchema groupSchema = Instancio.create(GroupSchema.class);
            groupSchema.setMembers(new HashSet<>()); 
            UserSchema userSchema = Instancio.create(UserSchema.class);
            Group group = Instancio.create(Group.class);

            when(jpaGroupRepository.findById(groupId)).thenReturn(Optional.of(groupSchema));
            when(jpaUserRepository.findById(memberId)).thenReturn(Optional.of(userSchema));
            when(jpaGroupRepository.save(groupSchema)).thenReturn(groupSchema);
            mapperMock.when(() -> GroupMapper.toModel(groupSchema)).thenReturn(group);

            Group result = groupRepository.addMemberToGroup(groupId, memberId);

            assertNotNull(result);
            verify(jpaGroupRepository).save(groupSchema);
        }
    }

    @Test
    void addMemberToGroup_notFound_returnsNull() {
        when(jpaGroupRepository.findById(anyString())).thenReturn(Optional.empty());

        Group result = groupRepository.addMemberToGroup("group-1", "user-1");

        assertNull(result);
    }

    @Test
    void addMemberToGroup_alreadyExists_throwsException() {
        String groupId = "group-1";
        String memberId = "user-1";
        
        UserSchema userSchema = Instancio.create(UserSchema.class);
        GroupSchema groupSchema = Instancio.create(GroupSchema.class);
        groupSchema.setMembers(new HashSet<>());
        groupSchema.getMembers().add(userSchema);

        when(jpaGroupRepository.findById(groupId)).thenReturn(Optional.of(groupSchema));
        when(jpaUserRepository.findById(memberId)).thenReturn(Optional.of(userSchema));

        ResourceExistException ex = assertThrows(ResourceExistException.class,
            () -> groupRepository.addMemberToGroup(groupId, memberId));

        assertEquals(EXISTING_FRIEND_IN_THE_GROUP, ex.getMessage());
    }

    @Test
    void getGroupsByUserId_success() {
        try (MockedStatic<GroupMapper> mapperMock = Mockito.mockStatic(GroupMapper.class)) {
            String userId = "user-1";
            Set<GroupSchema> schemas = new HashSet<>();
            schemas.add(Instancio.create(GroupSchema.class));

            when(jpaGroupRepository.findByMembers_Id(userId)).thenReturn(Optional.of(schemas));
            mapperMock.when(() -> GroupMapper.toModel(any(GroupSchema.class))).thenReturn(Instancio.create(Group.class));

            Set<Group> result = groupRepository.getGroupsByUserId(userId);

            assertNotNull(result);
            assertEquals(1, result.size());
        }
    }
}