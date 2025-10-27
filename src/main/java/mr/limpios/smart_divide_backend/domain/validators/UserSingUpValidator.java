package mr.limpios.smart_divide_backend.domain.validators;

import mr.limpios.smart_divide_backend.domain.models.User;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.MISSING_REQUIRED_FIELDS;
import static mr.limpios.smart_divide_backend.domain.constants.ExceptionsConstants.INVALID_EMAIL_FORMAT;

public class UserSingUpValidator {

    public static void validate(User user) throws IllegalArgumentException {

        if (isAllRequiredDataFilled(user)) {
            throw new IllegalArgumentException(MISSING_REQUIRED_FIELDS);
        }

        if (!isEmailFormatValird(user.email())) {
            throw new IllegalArgumentException(INVALID_EMAIL_FORMAT);
        }
    }

    public static boolean isAllRequiredDataFilled(User user) throws IllegalArgumentException {
        return (user.name().isBlank() && user.lastName().isBlank() && user.email().isBlank()
                && user.password().isBlank());
    }

    public static boolean isEmailFormatValird(String email) {
        String emailPattern = "^[\\w\\.-]+@[\\w\\.-]+\\.\\w+";
        return email.matches(emailPattern);
    }
}
