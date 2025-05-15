package com.krupenko.demo.service.account;

import com.krupenko.demo.entity.Account;
import com.krupenko.demo.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class AccountBalanceScheduler {

    private final AccountRepository accountRepository;
    private final AccountBalanceUpdaterService accountBalanceUpdaterService;

    @Scheduled(fixedDelay = 30000)
    public void updateAccountBalances() {
        List<Account> accounts = accountRepository.findAll();
        for (Account account : accounts) {
            accountBalanceUpdaterService.updateAccountBalance(account);
        }
    }

}
