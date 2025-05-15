package com.krupenko.demo.dto.phonedata;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PhoneDataCreateEditDto {

    private String phone;
    private Long userId;

}
