package com.krupenko.demo.service.account;

import com.krupenko.demo.dto.account.AccountTransferDto;
import com.krupenko.demo.entity.Account;
import com.krupenko.demo.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Objects;

import static com.krupenko.demo.constansts.ExceptionMessages.RECIPIENT_ACCOUNT_IS_NOT_FOUND;
import static com.krupenko.demo.constansts.ExceptionMessages.SENDER_ACCOUNT_IS_NOT_FOUND;
import static com.krupenko.demo.constansts.ExceptionMessages.SENDER_BALANCE_IS_TOO_LOW;
import static com.krupenko.demo.constansts.ExceptionMessages.TRANSFER_TO_YOURSELF;

@Log4j2
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    public void transfer(AccountTransferDto accountTransferDto) {
        BigDecimal value = accountTransferDto.getValue();
        Long senderUserId = accountTransferDto.getSenderUserId();
        Long recipientUserId = accountTransferDto.getRecipientUserId();
        if (Objects.equals(senderUserId, recipientUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, TRANSFER_TO_YOURSELF);
        }

        Account recipientAccount = accountRepository.findByUserIdWithLock(recipientUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, RECIPIENT_ACCOUNT_IS_NOT_FOUND));

        Account senderAccount = accountRepository.findByUserIdWithLock(senderUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, SENDER_ACCOUNT_IS_NOT_FOUND));

        if (senderAccount.getBalance().compareTo(value) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, SENDER_BALANCE_IS_TOO_LOW);
        }

        senderAccount.setBalance(senderAccount.getBalance().subtract(value));
        recipientAccount.setBalance(recipientAccount.getBalance().add(value));
        accountRepository.save(senderAccount);
        accountRepository.save(recipientAccount);
    }

}
