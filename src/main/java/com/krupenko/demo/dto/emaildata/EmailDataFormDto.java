package com.krupenko.demo.dto.emaildata;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.krupenko.demo.constansts.ExceptionMessages.BLANK_EMAIL_ADDRESS_VALIDATION_MSG;
import static com.krupenko.demo.constansts.ExceptionMessages.INCORRECT_EMAIL_ADDRESS_FORMAT_VALIDATION_MSG;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailDataFormDto {

    @NotBlank(message = BLANK_EMAIL_ADDRESS_VALIDATION_MSG)
    @Email(message = INCORRECT_EMAIL_ADDRESS_FORMAT_VALIDATION_MSG)
    private String email;

}
