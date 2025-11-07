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

    @Value("${app.jwt.refresh-secret:default-refresh-secret}")
    private String refreshSecret;

    @Value("${app.jwt.refresh-expiration:604800000}")
    private long refreshExpirationMs;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration}") long expirationMs) {
        byte[] keyBytes = Decoders.BASE64.decode(toBase64IfNeeded(secret));
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
    }

    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(userDetails);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Key refreshKey = getRefreshKey();
        return Jwts.builder()
                .setClaims(Map.of())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationMs))
                .signWith(refreshKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(Map.of())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        return isTokenValid(token, userDetails, false);
    }

    public boolean isTokenValid(String token, UserDetails userDetails, boolean isRefresh) {
        String username = extractUsername(token, isRefresh);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token, isRefresh);
    }

    public String extractUsername(String token) {
        return extractUsername(token, false);
    }

    public String extractUsername(String token, boolean isRefresh) {
        return extractAllClaims(token, isRefresh).getSubject();
    }

    private boolean isTokenExpired(String token, boolean isRefresh) {
        return extractAllClaims(token, isRefresh).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token, boolean isRefresh) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(isRefresh ? getRefreshKey() : key)
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

    private Key getRefreshKey() {
        byte[] keyBytes = Decoders.BASE64.decode(toBase64IfNeeded(refreshSecret));
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String toBase64IfNeeded(String s) {
        try {
            Decoders.BASE64.decode(s);
            return s;
        } catch (Exception e) {
            return java.util.Base64.getEncoder().encodeToString(s.getBytes());
        }
    }
}

