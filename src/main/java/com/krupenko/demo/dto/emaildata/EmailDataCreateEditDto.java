package com.krupenko.demo.dto.emaildata;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailDataCreateEditDto {

    private String email;
    private Long userId;

}
