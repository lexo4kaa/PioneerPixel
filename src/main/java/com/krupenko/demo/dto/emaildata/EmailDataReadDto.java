package com.krupenko.demo.dto.emaildata;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailDataReadDto {

    private Long id;
    private String email;

}
