package com.krupenko.demo.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountTransferDto {

    private Long senderUserId;
    private Long recipientUserId;
    private BigDecimal value;

}
