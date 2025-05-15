package com.krupenko.demo.validation;

import com.krupenko.demo.enums.LoginType;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class LoginValidator {

    private static final Pattern PHONE_PATTERN = Pattern.compile("[1-9][0-9]{0,12}");

    private final Validator validator;

    public LoginType detectLoginType(String login) {
        if (isEmail(login)) {
            return LoginType.EMAIL;
        } else if (isPhone(login)) {
            return LoginType.PHONE;
        } else {
            return LoginType.UNKNOWN;
        }
    }

    private boolean isEmail(String login) {
        return validator.validate(new EmailWrapper(login)).isEmpty();
    }

    private boolean isPhone(String login) {
        return PHONE_PATTERN.matcher(login).matches();
    }

    private record EmailWrapper(@Email String email) {
    }

}
