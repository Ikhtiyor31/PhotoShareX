package com.ikhtiyor.photosharex.user.service;

import io.jsonwebtoken.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AccessTokenServiceImpl implements AccessTokenService {

    private static final String jwtSecretKey = "asdLHUiafa3hoiuhohviuIIOIyiasdfa";
    private static final Logger LOGGER = LoggerFactory.getLogger(AccessTokenServiceImpl.class);

    @Override
    public String createAccessToken(String email) {
        return createToken(email, 10, ChronoUnit.MINUTES);
    }

    @Override
    public String createRefreshToken(String email) {
        return createToken(email, 15, ChronoUnit.DAYS);
    }

    @Override
    public String extractUsername(String token) {
        var claims = parseClaims(token);
        return claims.get("email").toString();
    }

    @Override
    public boolean isValidToken(String token) {
        try {
            var claims = parseClaims(token);
            return claims.getExpiration().after(Date.from(Instant.now()));
        } catch (ExpiredJwtException exception) {
            LOGGER.error("Token Expired {}", exception.getMessage());
            return false;
        } catch (JwtException exception) {
            LOGGER.error("Token Tampered {}", exception.getMessage());
            return false;
        } catch (NullPointerException exception) {
            LOGGER.error("Token is null {}", exception.getMessage());
            return false;
        }
    }

    private String createToken(String email, long amountToAdd, ChronoUnit chronoUnit) {
        return Jwts.builder()
            .setClaims(createClaims(email))
            .setSubject(email)
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(Date.from(Instant.now().plus(amountToAdd, chronoUnit)))
            .signWith(SignatureAlgorithm.HS256, jwtSecretKey)
            .compact();
    }

    private static Claims parseClaims(String token) {
        return Jwts.parser()
            .setSigningKey(jwtSecretKey)
            .parseClaimsJws(token)
            .getBody();
    }

    private static Map<String, Object> createClaims(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", username);
        return claims;
    }
}
