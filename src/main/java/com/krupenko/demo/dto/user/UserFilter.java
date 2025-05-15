package com.krupenko.demo.dto.user;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import static com.krupenko.demo.constansts.ExceptionMessages.INCORRECT_EMAIL_ADDRESS_FORMAT_VALIDATION_MSG;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class UserFilter {

    @Parameter(description = "Date of birth")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfBirth;

    @Parameter(description = "Phone number")
    private String phone;

    @Parameter(description = "Name")
    private String name;

    @Parameter(description = "Email address")
    @Email(message = INCORRECT_EMAIL_ADDRESS_FORMAT_VALIDATION_MSG)
    private String email;

    @Parameter(description = "Page number for pagination")
    @Min(0)
    private int page = 0;

    @Parameter(description = "Page size for pagination")
    @Min(1)
    private int size = 10;

}
