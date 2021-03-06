package by.epam.baranovsky.banking.constant;

/**
 * Utility class that stores constant strings
 * that represent error messages.
 * @author Baranovsky E. K.
 * @version 1.0.0
 */
public final class Message {

    public static final String LOGIN_EXCEPTION="login_exception";
    public static final String WRONG_EMAIL_OR_PASS="wrong_email_or_password";
    public static final String NO_SUCH_USER ="no_such_user";

    public static final String WRONG_INPUT="wrong_input";
    public static final String REGISTER_PASS_MISMATCH="register_password_mismatch";
    public static final String REGISTER_FAILED="register_failed";
    public static final String REGISTER_EXCEPTION="register_exception";
    public static final String USER_EXISTS="user_already_exists";
    public static final String USER_BANNED="user_is_banned";

    public static final String EDIT_USER_PASS_MISMATCH="edit_user_password_mismatch";
    public static final String EDIT_USER_EXCEPTION="edit_user_exception";

    public static final String TOO_MANY_CREATION_REQUESTS="too_many_requests_to_create_acc";
    public static final String AMBIGUOUS_USER_DATA="ambiguous_user_data";
    public static final String ONLY_USER="you_are_the_last_user_of_account";
    public static final String WRONG_NEW_STATUS="new_status_is_invalid";
    public static final String WRONG_NEW_ROLE="new_role_is_invalid";
    public static final String NOT_YOUR_ACCOUNT="this_account_does_not_belong_to_you";
    public static final String ACCOUNT_INFO_ERROR="account_info_error";
    public static final String CARD_CREATE_EXCEPTION="card_create_exception";
    public static final String TOO_MANY_CARDS= "card_limit_reached";
    public static final String ACCOUNT_LOCKED="account_is_locked";
    public static final String CARD_NOT_YOURS="card_is_not_yours";
    public static final String CANT_ACCESS_CARD_INFO="access_to_card_denied";
    public static final String CREDIT_CARD= "this_is_a_credit_card";
    public static final String CANNOT_ADD_SELF= "cannot_add_self";

    public static final String PENALTY_BILL_INTERSECTION="penalty_and_bill_intersect";

    public static final String OPERATION_INVALID_VALUE="operation_invalid_value";
    public static final String OPERATION_ILLEGAL ="illegal_operation";
    public static final String OPERATION_NOT_ENOUGH_DATA="operation_transfer_to_self";
    public static final String OPERATION_TRANSFER_TO_SELF="operation_transfer_to_self";
    public static final String NO_SUCH_RECEIVER="no_such_recipient";
    public static final String NO_MONEY="not_enough_money";
    public static final String ACC_SUSPENDED="this_account_is_suspended";
    public static final String ACC_LOCKED="this_account_is_locked";
    public static final String CARD_LOCKED="this_card_is_locked";


    public static final String ACCOUNT_NOT_PENDING="this_account_is_not_pending";
    public static final String CANT_ALTER_EXPIRED_CARD ="cant_alter_expired";
    public static final String NOT_ENOUGH_RIGHTS="you_have_no_power_here";

    public static final String BILL_TO_SELF="cannot_add_bills_to_self";
    public static final String TOO_MANY_BILL_REQUESTS="too_many_bill_requests";
    public static final String DUE_DATE_TOO_CLOSE="due_date_too_close";
    public static final String BILL_CANNOT_DELETE="cannot_delete_bill";
    public static final String BILL_CANNOT_APPROVE="cannot_approve_bill";
    public static final String ACCOUNT_LOCKED_OR_NOT_YOURS="acc_blocked_or_not_yours";

    public static final String LOAN_TOO_SHORT_OR_LONG ="loan_too_short_or_long";
    public static final String LOAN_VALUE_TOO_SMALL="loan_too_small";
    public static final String LOAN_NOT_FOR_YOUR_ACCOUNT="loan_not_your_acc";
    public static final String TOO_MANY_LOANS="too_many_loans";

}
