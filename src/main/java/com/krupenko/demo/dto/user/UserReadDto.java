package com.krupenko.demo.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserReadDto {

    private Long id;
    private String name;
    private LocalDate dateOfBirth;
    private BigDecimal balance;
    private Set<String> emails;
    private Set<String> phones;

}
