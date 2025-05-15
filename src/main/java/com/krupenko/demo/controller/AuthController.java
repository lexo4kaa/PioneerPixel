package com.krupenko.demo.controller;

import com.krupenko.demo.dto.LoginDto;
import com.krupenko.demo.dto.user.UserReadDto;
import com.krupenko.demo.enums.LoginType;
import com.krupenko.demo.service.TokenService;
import com.krupenko.demo.service.UserService;
import com.krupenko.demo.validation.LoginValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static com.krupenko.demo.constansts.ExceptionMessages.INVALID_LOGIN_FORMAT;
import static com.krupenko.demo.constansts.ExceptionMessages.INVALID_USERNAME_OR_PASSWORD;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Authorization", description = "API for user login and logout")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserService userService;
    private final LoginValidator loginValidator;

    @Operation(summary = "Login", description = "Login with the given credentials. " +
            "As a login field, an email address or phone number can be used.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDto body, HttpServletResponse response) {
        String login = body.getLogin();
        Long userId = getUserByLogin(login).getId();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, body.getPassword());
        authenticationManager.authenticate(authentication);
        String token = tokenService.generateToken(userId);
        tokenService.saveTokenInCookie(response, token);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Logout", description = "Logout the current user.")
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        throw new IllegalStateException("Logout is handled by Spring Security. Controller method should not be called.");
    }

    private UserReadDto getUserByLogin(String login) {
        LoginType loginType = loginValidator.detectLoginType(login);
        if (LoginType.UNKNOWN == loginType) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, INVALID_LOGIN_FORMAT);
        }
        Optional<UserReadDto> user = switch (loginType) {
            case EMAIL -> userService.findByEmail(login);
            case PHONE -> userService.findByPhone(login);
            default -> Optional.empty();
        };
        return user.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, INVALID_USERNAME_OR_PASSWORD));
    }

}
