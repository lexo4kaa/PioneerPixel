package com.krupenko.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import static com.krupenko.demo.constansts.ExceptionMessages.BLANK_EMAIL_ADDRESS_VALIDATION_MSG;
import static com.krupenko.demo.constansts.ExceptionMessages.BLANK_LOGIN_VALIDATION_MSG;
import static com.krupenko.demo.constansts.ExceptionMessages.BLANK_PASSWORD_VALIDATION_MSG;

@Data
@AllArgsConstructor
public class LoginDto {

    @NotBlank(message = BLANK_LOGIN_VALIDATION_MSG)
    private String login;

    @NotBlank(message = BLANK_PASSWORD_VALIDATION_MSG)
    private String password;

}
