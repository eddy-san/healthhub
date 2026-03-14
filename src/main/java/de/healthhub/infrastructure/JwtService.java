package de.healthhub.infrastructure;

import de.healthhub.model.domain.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.enterprise.context.ApplicationScoped;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@ApplicationScoped
public class JwtService {

    private String secret() {
        String value = System.getenv("JWT_SECRET");
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("JWT_SECRET environment variable is missing");
        }
        return value;
    }

    public long getExpirationSeconds() {
        String value = System.getenv("JWT_EXPIRATION_SECONDS");
        if (value == null || value.isBlank()) {
            return 3600L;
        }
        return Long.parseLong(value);
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret().getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + getExpirationSeconds() * 1000);

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", user.getRoles().stream()
                        .map(role -> role.getRoleName().name())
                        .toList())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key())
                .compact();
    }

    public boolean isValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
}