package mr.limpios.smart_divide_backend.infrastructure.repositories.impl;

import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.FRIENDSHIP_ALREADY_EXISTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import mr.limpios.smart_divide_backend.domain.models.Friendship;
import mr.limpios.smart_divide_backend.infrastructure.mappers.FriendshipMapper;
import mr.limpios.smart_divide_backend.infrastructure.repositories.jpa.JPAFriendShipRepository;
import mr.limpios.smart_divide_backend.infrastructure.schemas.FriendshipSchema;

@ExtendWith(MockitoExtension.class)
class FriendShipRepositoryImpTest {

    @Mock
    private JPAFriendShipRepository jpaRepository;

    @InjectMocks
    private FriendShipRepositoryImp repository;

    @Test
    void createFriendRequest_success() {
        try (MockedStatic<FriendshipMapper> mapperMock = Mockito.mockStatic(FriendshipMapper.class)) {
            Friendship friendship = Instancio.create(Friendship.class);
            FriendshipSchema schema = Instancio.create(FriendshipSchema.class);

            when(jpaRepository.existsByRequesterIdAndFriendIdOrRequesterIdAndFriendId(
                anyString(), anyString(), anyString(), anyString())).thenReturn(false);
            
            mapperMock.when(() -> FriendshipMapper.toSchema(friendship)).thenReturn(schema);

            repository.createFriendRequest(friendship);

            verify(jpaRepository).save(schema);
        }
    }

    @Test
    void createFriendRequest_alreadyExists_throwsException() {
        Friendship friendship = Instancio.create(Friendship.class);

        when(jpaRepository.existsByRequesterIdAndFriendIdOrRequesterIdAndFriendId(
            anyString(), anyString(), anyString(), anyString())).thenReturn(true);

        ResourceExistException ex = assertThrows(ResourceExistException.class,
            () -> repository.createFriendRequest(friendship));

        assertEquals(FRIENDSHIP_ALREADY_EXISTS, ex.getMessage());
        verify(jpaRepository, never()).save(any());
    }

    @Test
    void getAllFriendshipsByUserId_success() {
        try (MockedStatic<FriendshipMapper> mapperMock = Mockito.mockStatic(FriendshipMapper.class)) {
            String userId = "user-1";
            int size = 3;
            Set<FriendshipSchema> schemas = Instancio.ofSet(FriendshipSchema.class).size(size).create();

            when(jpaRepository.findByRequesterIdOrFriendId(userId, userId)).thenReturn(schemas);
            
            mapperMock.when(() -> FriendshipMapper.toModel(any(FriendshipSchema.class)))
                .thenAnswer(invocation -> Instancio.create(Friendship.class));

            Set<Friendship> result = repository.getAllFriendshipsByUserId(userId);

            assertNotNull(result);
            assertEquals(size, result.size());
        }
    }

    @Test
    void areFriends_sameId_returnsFalse() {
        String id = "user-1";
        
        Boolean result = repository.areFriends(id, id);

        assertFalse(result);
        verify(jpaRepository, never()).findConfirmedFriendship(anyString(), anyString());
    }

    @Test
    void areFriends_found_returnsTrue() {
        String ownerId = "user-1";
        String friendId = "user-2";
        FriendshipSchema schema = Instancio.create(FriendshipSchema.class);

        when(jpaRepository.findConfirmedFriendship(ownerId, friendId)).thenReturn(Optional.of(schema));

        Boolean result = repository.areFriends(ownerId, friendId);

        assertTrue(result);
    }

    @Test
    void areFriends_notFound_returnsFalse() {
        String ownerId = "user-1";
        String friendId = "user-2";

        when(jpaRepository.findConfirmedFriendship(ownerId, friendId)).thenReturn(Optional.empty());

        Boolean result = repository.areFriends(ownerId, friendId);

        assertFalse(result);
    }
}