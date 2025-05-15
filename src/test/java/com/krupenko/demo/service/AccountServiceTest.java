package com.krupenko.demo.service;

import com.krupenko.demo.dto.account.AccountTransferDto;
import com.krupenko.demo.entity.Account;
import com.krupenko.demo.entity.User;
import com.krupenko.demo.repository.AccountRepository;
import com.krupenko.demo.service.account.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

import static com.krupenko.demo.constansts.ExceptionMessages.RECIPIENT_ACCOUNT_IS_NOT_FOUND;
import static com.krupenko.demo.constansts.ExceptionMessages.SENDER_ACCOUNT_IS_NOT_FOUND;
import static com.krupenko.demo.constansts.ExceptionMessages.SENDER_BALANCE_IS_TOO_LOW;
import static com.krupenko.demo.constansts.ExceptionMessages.TRANSFER_TO_YOURSELF;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AccountServiceTest {

    private AccountRepository accountRepository;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountRepository = Mockito.mock(AccountRepository.class);
        accountService = new AccountService(accountRepository);
    }

    @Test
    void shouldTransferSuccessfully() {
        User senderUser = new User();
        senderUser.setId(1L);

        Account senderAccount = new Account();
        senderAccount.setUser(senderUser);
        senderAccount.setBalance(new BigDecimal("100.00"));

        User recipientUser = new User();
        recipientUser.setId(2L);

        Account recipientAccount = new Account();
        recipientAccount.setUser(recipientUser);
        recipientAccount.setBalance(new BigDecimal("50.00"));

        when(accountRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByUserIdWithLock(2L)).thenReturn(Optional.of(recipientAccount));

        AccountTransferDto dto = new AccountTransferDto();
        dto.setSenderUserId(1L);
        dto.setRecipientUserId(2L);
        dto.setValue(new BigDecimal("30.00"));

        accountService.transfer(dto);

        assertEquals(new BigDecimal("70.00"), senderAccount.getBalance());
        assertEquals(new BigDecimal("80.00"), recipientAccount.getBalance());

        verify(accountRepository).save(senderAccount);
        verify(accountRepository).save(recipientAccount);
    }

    @Test
    void shouldThrowIfTransferToYourself() {
        AccountTransferDto dto = new AccountTransferDto();
        dto.setSenderUserId(1L);
        dto.setRecipientUserId(1L);
        dto.setValue(new BigDecimal("10.00"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> accountService.transfer(dto));
        assertEquals(TRANSFER_TO_YOURSELF, ex.getReason());
    }

    @Test
    void shouldThrowIfRecipientNotFound() {
        when(accountRepository.findByUserIdWithLock(2L)).thenReturn(Optional.empty());
        when(accountRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(new Account()));

        AccountTransferDto dto = new AccountTransferDto();
        dto.setSenderUserId(1L);
        dto.setRecipientUserId(2L);
        dto.setValue(new BigDecimal("10.00"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> accountService.transfer(dto));
        assertEquals(RECIPIENT_ACCOUNT_IS_NOT_FOUND, ex.getReason());
    }

    @Test
    void shouldThrowIfSenderNotFound() {
        when(accountRepository.findByUserIdWithLock(2L)).thenReturn(Optional.of(new Account()));
        when(accountRepository.findByUserIdWithLock(1L)).thenReturn(Optional.empty());

        AccountTransferDto dto = new AccountTransferDto();
        dto.setSenderUserId(1L);
        dto.setRecipientUserId(2L);
        dto.setValue(new BigDecimal("10.00"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> accountService.transfer(dto));
        assertEquals(SENDER_ACCOUNT_IS_NOT_FOUND, ex.getReason());
    }

    @Test
    void shouldThrowIfSenderBalanceTooLow() {
        User senderUser = new User();
        senderUser.setId(1L);

        Account senderAccount = new Account();
        senderAccount.setUser(senderUser);
        senderAccount.setBalance(new BigDecimal("5.00"));

        User recipientUser = new User();
        recipientUser.setId(2L);

        Account recipientAccount = new Account();
        recipientAccount.setUser(recipientUser);
        recipientAccount.setBalance(new BigDecimal("50.00"));

        when(accountRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByUserIdWithLock(2L)).thenReturn(Optional.of(recipientAccount));

        AccountTransferDto dto = new AccountTransferDto();
        dto.setSenderUserId(1L);
        dto.setRecipientUserId(2L);
        dto.setValue(new BigDecimal("10.00"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> accountService.transfer(dto));
        assertEquals(SENDER_BALANCE_IS_TOO_LOW, ex.getReason());
    }
}

