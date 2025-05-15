package com.krupenko.demo.dto.account;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountTransferFormDto {

    @Parameter(description = "Recipient User Id")
    private Long recipientUserId;

    @Parameter(description = "Value to transfer")
    @DecimalMin(value = "0.01")
    private BigDecimal value;

}
