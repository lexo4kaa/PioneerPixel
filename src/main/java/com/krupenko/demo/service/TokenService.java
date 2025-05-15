package com.krupenko.demo.service;

import com.krupenko.demo.constansts.CookieParams;
import com.krupenko.demo.security.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtUtil jwtUtil;

    public Optional<Long> getUserIdFromRequest(HttpServletRequest request) {
        return getTokenFromCookie(request)
                .map(Cookie::getValue)
                .map(this::getUserIdFromToken);
    }

    public Optional<Cookie> getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return Optional.empty();
        }
        return Arrays.stream(cookies)
                .filter(cookie -> CookieParams.TOKEN.equals(cookie.getName()))
                .findFirst();
    }

    public Long getUserIdFromToken(String jwtToken) {
        return jwtUtil.extractUserId(jwtToken);
    }

    public boolean validateToken(String jwtToken, Long userId) {
        return jwtUtil.validateToken(jwtToken, userId);
    }

    public String generateToken(Long userId) {
        return jwtUtil.generateToken(userId);
    }

    public void saveTokenInCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(CookieParams.TOKEN, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

}
