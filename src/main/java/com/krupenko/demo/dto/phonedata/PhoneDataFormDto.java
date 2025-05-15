package com.krupenko.demo.dto.phonedata;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.krupenko.demo.constansts.ExceptionMessages.BLANK_PHONE_NUMBER_VALIDATION_MSG;
import static com.krupenko.demo.constansts.ExceptionMessages.INCORRECT_PHONE_NUMBER_FORMAT_VALIDATION_MSG;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhoneDataFormDto {

    @NotBlank(message = BLANK_PHONE_NUMBER_VALIDATION_MSG)
    @Pattern(regexp = "[1-9][0-9]{0,12}", message = INCORRECT_PHONE_NUMBER_FORMAT_VALIDATION_MSG)
    private String phone;

}
