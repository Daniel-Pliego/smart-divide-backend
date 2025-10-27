package mr.limpios.smart_divide_backend.infraestructure.mappers;

import mr.limpios.smart_divide_backend.domain.models.Card;
import mr.limpios.smart_divide_backend.domain.models.User;
import mr.limpios.smart_divide_backend.infraestructure.schemas.CardSchema;
import mr.limpios.smart_divide_backend.infraestructure.schemas.UserSchema;

import java.util.List;
import java.util.Optional;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class UserMapper {

    private UserMapper() {}

    public static UserSchema toSchema(User user) {

        Set<CardSchema> schemaCards = Optional.ofNullable(user.cards())
                .orElse(Collections.emptyList()) // si null → lista vacía
                .stream()
                .map(CardMapper::toSchema)
                .collect(Collectors.toSet());

        return new UserSchema(
                user.id(),
                user.name(),
                user.lastName(),
                user.email(),
                user.password(),
                user.photoUrl(),
                user.isVerified(),
                schemaCards
        );
    }

    public static User toModel(UserSchema userSchema) {

        List<Card> modelCards = Optional.ofNullable(userSchema.getCards())
                .orElse(Collections.emptySet()) // si null → set vacío
                .stream()
                .map(CardMapper::toModel)
                .collect(Collectors.toList());

        return new User(
                userSchema.getId(),
                userSchema.getName(),
                userSchema.getLastName(),
                userSchema.getEmail(),
                userSchema.getPassword(),
                userSchema.getPhotoUrl(),
                userSchema.getIsVerified(),
                modelCards
        );
    }
}
