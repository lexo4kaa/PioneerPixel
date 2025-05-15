package com.krupenko.demo.dto.phonedata;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PhoneDataReadDto {

    private Long id;
    private String phone;

}
