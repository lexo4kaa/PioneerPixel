package com.krupenko.demo.controller;

import com.krupenko.demo.dto.account.AccountTransferDto;
import com.krupenko.demo.dto.account.AccountTransferFormDto;
import com.krupenko.demo.service.account.AccountService;
import com.krupenko.demo.service.TokenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/account")
@Tag(name = "Account", description = "API for operations with user account")
public class AccountController extends BaseController {

    private final AccountService accountService;

    public AccountController(TokenService tokenService, AccountService accountService) {
        super(tokenService);
        this.accountService = accountService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@Valid @ParameterObject AccountTransferFormDto formData,
                                      HttpServletRequest request) {
        Long currentUserId = getUserIdFromRequest(request);
        AccountTransferDto accountTransferDto = new AccountTransferDto(currentUserId, formData.getRecipientUserId(),
                formData.getValue());
        accountService.transfer(accountTransferDto);
        return ResponseEntity.ok(null);
    }

}
