package mr.limpios.smart_divide_backend.infraestructure.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import mr.limpios.smart_divide_backend.domain.models.Group;
import mr.limpios.smart_divide_backend.domain.models.Payment;
import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.infraestructure.schemas.GroupSchema;
import mr.limpios.smart_divide_backend.infraestructure.schemas.PaymentSchema;
import mr.limpios.smart_divide_backend.infraestructure.schemas.UserSchema;

class PaymentMapperTest {

    @Test
    void toModel_mapsCorrectly() {
        try (MockedStatic<UserMapper> userMapperMock = Mockito.mockStatic(UserMapper.class);
             MockedStatic<GroupMapper> groupMapperMock = Mockito.mockStatic(GroupMapper.class)) {
            
            PaymentSchema schema = Instancio.create(PaymentSchema.class);
            User userModel = Instancio.create(User.class);
            Group groupModel = Instancio.create(Group.class);

            userMapperMock.when(() -> UserMapper.toModel(any(UserSchema.class)))
                .thenReturn(userModel);
            
            groupMapperMock.when(() -> GroupMapper.toModel(any(GroupSchema.class)))
                .thenReturn(groupModel);

            Payment result = PaymentMapper.toModel(schema);

            assertNotNull(result);
            assertEquals(schema.getId(), result.id());
            assertEquals(schema.getAmount(), result.amount());
            assertEquals(schema.getCreatedAt(), result.createdAt());
            
            assertEquals(userModel, result.fromUser());
            assertEquals(userModel, result.toUser());
            assertEquals(groupModel, result.group());

            userMapperMock.verify(() -> UserMapper.toModel(any(UserSchema.class)), Mockito.times(2));
            groupMapperMock.verify(() -> GroupMapper.toModel(any(GroupSchema.class)));
        }
    }

    @Test
    void toSchema_mapsCorrectly() {
        try (MockedStatic<UserMapper> userMapperMock = Mockito.mockStatic(UserMapper.class);
             MockedStatic<GroupMapper> groupMapperMock = Mockito.mockStatic(GroupMapper.class)) {
            
            Payment payment = Instancio.create(Payment.class);
            UserSchema userSchema = Instancio.create(UserSchema.class);
            GroupSchema groupSchema = Instancio.create(GroupSchema.class);

            userMapperMock.when(() -> UserMapper.toSchema(any(User.class)))
                .thenReturn(userSchema);
            
            groupMapperMock.when(() -> GroupMapper.toSchema(any(Group.class)))
                .thenReturn(groupSchema);

            PaymentSchema result = PaymentMapper.toSchema(payment);

            assertNotNull(result);
            assertEquals(payment.id(), result.getId());
            assertEquals(payment.amount(), result.getAmount());
            assertEquals(payment.createdAt(), result.getCreatedAt());

            assertEquals(userSchema, result.getFromUser());
            assertEquals(userSchema, result.getToUser());
            assertEquals(groupSchema, result.getGroup());

            userMapperMock.verify(() -> UserMapper.toSchema(any(User.class)), Mockito.times(2));
            groupMapperMock.verify(() -> GroupMapper.toSchema(any(Group.class)));
        }
    }
}