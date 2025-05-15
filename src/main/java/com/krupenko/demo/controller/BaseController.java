package com.krupenko.demo.controller;

import com.krupenko.demo.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
public abstract class BaseController {

    private final TokenService tokenService;

    protected Long getUserIdFromRequest(HttpServletRequest request) {
        return tokenService.getUserIdFromRequest(request)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

}
