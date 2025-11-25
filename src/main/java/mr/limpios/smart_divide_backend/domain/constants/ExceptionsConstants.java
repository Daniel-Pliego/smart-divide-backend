package mr.limpios.smart_divide_backend.domain.constants;

public class ExceptionsConstants {
    public static final String USER_NOT_FOUND = "User not found";
    public static final String GROUP_NOT_FOUND = "Group not found";
    public static final String DATA_REQUIRED = "Data is required";
    public static final String MISSING_REQUIRED_FIELDS = "Missing required fields";
    public static final String INVALID_EMAIL_FORMAT = "Invalid email format";
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
    public static final String FRIENDSHIP_ALREADY_EXISTS = "Friendship already exists between these users";
    public static final String FRIENDSHIP_NOT_FOUND = "Friendship not found";
    public static final String EXISTING_FRIEND_IN_THE_GROUP = "A friend has already been added to the group";
    public static final String GROUPS_NOT_FOUND_FOR_USER = "No groups found for the user";
    public static final String USER_NOT_MEMBER_OF_GROUP = "User is not a member of the group";
    public static final String DEBTORS_NOT_IN_GROUP = "One or more debtors are not in the group";
    public static final String PAYERS_NOT_IN_GROUP = "One or more payers are not in the group";
    public static final String DEBTORS_AMOUNT_MISMATCH = "The sum of debtors amounts does not equal the total expense amount";
    public static final String PAYERS_AMOUNT_MISMATCH = "The sum of payers amounts does not equal the total expense amount";
    public static final String EQUAL_DIVISION_AMOUNT_MISMATCH = "For EQUAL division, each debtor must pay the same amount";
    public static final String UNAUTHORIZED_ACCESS = "Unauthorized access";
    public static final String AMOUNT_MUST_BE_POSITIVE = "Amount must be greater than zero";
    public static final String USER_CANNOT_PAY_SELF = "A user cannot make a payment to themselves";
    public static final String PAYER_NOT_THE_SAME_AS_AUTHENTICATED_USER = "The payer must be the same as the authenticated user";
    public static final String BOTH_USERS_MUST_BE_MEMBERS_OF_GROUP = "Both users must be members of the group";
    public static final String NO_EXISTING_DEBTS_FOR_USER_PAIR = "No existing debts found for this user pair in the group";
    public static final String PAYMENT_AMOUNT_EXCEEDS_DEBT = "Payment amount exceeds existing debt";
    public static final String EXPENSE_NOT_FOUND = "Expense not found";
}