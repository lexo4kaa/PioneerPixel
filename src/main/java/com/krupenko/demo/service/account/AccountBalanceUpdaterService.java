package com.krupenko.demo.service.account;

import com.krupenko.demo.entity.Account;
import com.krupenko.demo.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;

import static com.krupenko.demo.constansts.RedisConstants.INITIAL_BALANCE_KEY_PREFIX;
import static com.krupenko.demo.constansts.RedisConstants.USERS_BY_FILTER_CACHE_NAME;

@Log4j2
@Service
@RequiredArgsConstructor
public class AccountBalanceUpdaterService {

    private static final BigDecimal ITERATION_STEP_COEFFICIENT = new BigDecimal("1.1");
    private static final BigDecimal MAX_ALLOWED_BALANCE_COEFFICIENT = new BigDecimal("2.07");

    private final RedisTemplate<String, String> redisTemplate;
    private final AccountRepository accountRepository;

    @Transactional
    @Async("taskExecutor")
    public void updateAccountBalance(Account account) {
        try {
            String key = INITIAL_BALANCE_KEY_PREFIX + account.getUser().getId();
            String initialBalanceStr = redisTemplate.opsForValue().get(key);

            BigDecimal initialBalance;
            if (initialBalanceStr != null) {
                initialBalance = new BigDecimal(initialBalanceStr);
            } else {
                initialBalance = account.getBalance();
                if (initialBalance != null && initialBalance.compareTo(BigDecimal.ZERO) > 0) {
                    log.info("User {} initial balance not found, setting it now.", account.getUser().getId());
                    redisTemplate.opsForValue().set(key, initialBalance.toString());
                    removeUserBasedKeys();
                } else {
                    log.error("User {} has no valid balance.", account.getUser().getId());
                    return;
                }
            }
            updateBalanceAndSave(account, initialBalance);
        } catch (Exception e) {
            log.error("Error occurred while updating account {}: {}", account.getId(), e.getMessage(), e);
        }
    }

    private void removeUserBasedKeys() {
        Set<String> keys = redisTemplate.keys(USERS_BY_FILTER_CACHE_NAME + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    private void updateBalanceAndSave(Account account, BigDecimal initialDeposit) {
        accountRepository.findByIdWithLock(account.getId()).ifPresent(lockedAccount -> {
            updateBalance(lockedAccount, initialDeposit);
            accountRepository.save(lockedAccount);
        });
    }

    private void updateBalance(Account account, BigDecimal initialDeposit) {
        BigDecimal increasedBalance = account.getBalance().multiply(ITERATION_STEP_COEFFICIENT);
        BigDecimal maxAllowedBalance = initialDeposit.multiply(MAX_ALLOWED_BALANCE_COEFFICIENT);

        if (increasedBalance.compareTo(maxAllowedBalance) < 0) {
            account.setBalance(increasedBalance);
        }
    }

}

