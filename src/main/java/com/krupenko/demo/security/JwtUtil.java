package com.krupenko.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

@Log4j2
@Component
public class JwtUtil {
    private final SecretKey secretKey;

    @Value("${jwt.expiration-time}")
    private long expirationTime;

    public JwtUtil() {
        this.secretKey = Jwts.SIG.HS256.key().build();
    }

    public String generateToken(Long userId) {
        long timeMillis = System.currentTimeMillis();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(new Date(timeMillis))
                .expiration(new Date(timeMillis + expirationTime))
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token, Long userId) {
        final Long extractedUserId = extractUserId(token);
        return extractedUserId != null && extractedUserId.equals(userId) && !isTokenExpired(token);
    }

    public Long extractUserId(String token) {
        try {
            return Optional.ofNullable(parseClaims(token))
                    .map(claims -> Long.parseLong(claims.getSubject()))
                    .orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        return expiration == null || expiration.before(new Date());
    }

    private Date extractExpiration(String token) {
        return Optional.ofNullable(parseClaims(token))
                .map(Claims::getExpiration)
                .orElse(null);
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.debug("Invalid JWT token or unable to parse claims: {}", e.getMessage());
            return null;
        }
    }
}
