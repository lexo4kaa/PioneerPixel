package com.krupenko.demo.constansts;

public class ExceptionMessages {
    private ExceptionMessages() {
    }

    public static final String TRANSFER_TO_YOURSELF = "Transfer to yourself doesn't make sense.";
    public static final String RECIPIENT_ACCOUNT_IS_NOT_FOUND = "Recipient account is not found.";
    public static final String SENDER_ACCOUNT_IS_NOT_FOUND = "Sender account is not found.";
    public static final String SENDER_BALANCE_IS_TOO_LOW = "Sender's balance is too low.";

    public static final String INVALID_LOGIN_FORMAT = "Invalid login format. Expected email or phone number.";
    public static final String INVALID_USERNAME_OR_PASSWORD = "Invalid username or password.";

    public static final String TRY_TO_DELETE_LAST_EMAIL_ADDRESS = "You can't delete your last email address.";
    public static final String EMAIL_ADDRESS_IS_ALREADY_IN_USE = "Entered email address is already in use.";
    public static final String TRY_TO_INFLUENCE_OTHER_EMAIL_ADDRESS = "You cannot influence the email address of another user.";

    public static final String TRY_TO_DELETE_LAST_PHONE_NUMBER = "You can't delete your last phone number.";
    public static final String PHONE_NUMBER_IS_ALREADY_IN_USE = "Entered phone number is already in use.";
    public static final String TRY_TO_INFLUENCE_OTHER_PHONE_NUMBER = "You cannot influence the phone number of another user.";

    public static final String BLANK_EMAIL_ADDRESS_VALIDATION_MSG = "The email address cannot be blank.";
    public static final String INCORRECT_EMAIL_ADDRESS_FORMAT_VALIDATION_MSG = "The email address must be in the correct format.";
    public static final String BLANK_PHONE_NUMBER_VALIDATION_MSG = "The phone number cannot be blank.";
    public static final String INCORRECT_PHONE_NUMBER_FORMAT_VALIDATION_MSG = "The phone number must contain from 1 to 13 digits. The first digit must not be 0.";

    public static final String BLANK_LOGIN_VALIDATION_MSG = "The login cannot be blank.";
    public static final String BLANK_PASSWORD_VALIDATION_MSG = "The password cannot be blank.";

}
