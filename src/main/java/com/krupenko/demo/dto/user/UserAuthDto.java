package com.krupenko.demo.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class UserAuthDto {

    private Long id;
    private String password;

}
