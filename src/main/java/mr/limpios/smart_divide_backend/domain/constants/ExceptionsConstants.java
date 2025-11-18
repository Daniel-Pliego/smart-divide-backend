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
    public static final String EQUAL_DIVISION_AMOUNT_MISMATCH = "For EQUAL division, each debtor must pay the same amount";}