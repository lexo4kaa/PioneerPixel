package com.krupenko.demo.it;

import com.krupenko.demo.constansts.CookieParams;
import com.krupenko.demo.security.JwtUtil;
import com.krupenko.demo.entity.Account;
import com.krupenko.demo.entity.User;
import com.krupenko.demo.repository.AccountRepository;
import com.krupenko.demo.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.krupenko.demo.constansts.ExceptionMessages.RECIPIENT_ACCOUNT_IS_NOT_FOUND;
import static com.krupenko.demo.constansts.ExceptionMessages.SENDER_BALANCE_IS_TOO_LOW;
import static com.krupenko.demo.constansts.ExceptionMessages.TRANSFER_TO_YOURSELF;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class TransferIntegrationTest {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final MockMvc mockMvc;

    private Long senderId;
    private Long recipientId;

    @Autowired
    public TransferIntegrationTest(AccountRepository accountRepository, UserRepository userRepository, JwtUtil jwtUtil,
                                   MockMvc mockMvc) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.mockMvc = mockMvc;
    }

    @BeforeEach
    void initAccounts() {
        accountRepository.deleteAll();
        userRepository.deleteAll();

        Account senderAccount = createAccount("Sender", new BigDecimal("100.00"));
        senderId = senderAccount.getUser().getId();

        Account recipientAccount = createAccount("Recipient", new BigDecimal("50.00"));
        recipientId = recipientAccount.getUser().getId();
    }

    private Account createAccount(String name, BigDecimal balance) {
        User user = new User();
        user.setName(name);
        user.setDateOfBirth(LocalDate.now());
        user.setPassword(name);
        userRepository.save(user);

        Account account = new Account();
        account.setUser(user);
        account.setBalance(balance);
        accountRepository.save(account);
        return account;
    }

    @Test
    void shouldTransferSuccessfully() throws Exception {
        performTransfer(recipientId, "30.00")
                .andExpect(status().isOk());

        Account senderAccount = accountRepository.findByUserId(senderId).orElseThrow();
        Account recipientAccount = accountRepository.findByUserId(recipientId).orElseThrow();

        assertEquals(new BigDecimal("70.00"), senderAccount.getBalance());
        assertEquals(new BigDecimal("80.00"), recipientAccount.getBalance());
    }

    @Test
    void shouldFailIfTransferringToYourself() throws Exception {
        performTransfer(senderId, "30.00")
                .andExpect(status().isBadRequest())
                .andExpect(content().string(TRANSFER_TO_YOURSELF));
    }

    @Test
    void shouldFailIfBalanceTooLow() throws Exception {
        performTransfer(recipientId, "999.99")
                .andExpect(status().isBadRequest())
                .andExpect(content().string(SENDER_BALANCE_IS_TOO_LOW));
    }

    @Test
    void shouldFailIfRecipientNotFound() throws Exception {
        performTransfer(-1L, "30.00")
                .andExpect(status().isBadRequest())
                .andExpect(content().string(RECIPIENT_ACCOUNT_IS_NOT_FOUND));
    }

    private ResultActions performTransfer(Long recipientId, String amount) throws Exception {
        String token = jwtUtil.generateToken(senderId);
        return mockMvc.perform(post("/api/v1/account/transfer")
                .param("recipientUserId", recipientId.toString())
                .param("value", amount)
                .cookie(new Cookie(CookieParams.TOKEN, token)));
    }

}

