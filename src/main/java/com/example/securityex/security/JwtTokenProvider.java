package com.example.securityex.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static java.time.LocalDateTime.now;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationSeconds}")
    private int jwtExpirationSeconds;

    public String generateToken(final Authentication authentication) {

        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();

        LocalDateTime expiryDate = now().plusSeconds(jwtExpirationSeconds * 1000);

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(expiryDate.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUserEmailFromJwt(final String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(final String authToken) {
        return Try.of(() -> Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken))
                .map(claimsJws -> true)
                .getOrElse(false);
    }
}
