package com.example.orderservice.config;

import com.example.orderservice.security.JwtAuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    private final Key key;
    private final long expirationMs;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration}") long expirationMs) {
        byte[] keyBytes = Decoders.BASE64.decode(toBase64IfNeeded(secret));
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
    }

    private String toBase64IfNeeded(String s) {
        try {
            Decoders.BASE64.decode(s);
            return s;
        } catch (Exception e) {
            return java.util.Base64.getEncoder().encodeToString(s.getBytes());
        }
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(Map.of())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String u = extractUsername(token);
        return u.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new JwtAuthenticationException("JWT expired");
        } catch (UnsupportedJwtException e) {
            throw new JwtAuthenticationException("Unsupported JWT");
        } catch (MalformedJwtException e) {
            throw new JwtAuthenticationException("Malformed JWT");
        } catch (SignatureException e) {
            throw new JwtAuthenticationException("Invalid JWT signature");
        } catch (IllegalArgumentException e) {
            throw new JwtAuthenticationException("JWT token is empty or null");
        }
    }
}

