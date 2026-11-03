package com.tripsync.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey singingKey;
    private final Long expirationMs;

    public JwtService(
            @Value("${app.jwt.secret}")String secret,
            @Value("${app.jwt.expiration-ms}") Long expirationMs){
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.singingKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
    }
    public String generateToken(String email){
        Date now = new Date();
        Date expiry = new Date(now.getTime()+expirationMs);
        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(singingKey)
                .compact();
    }

    public  String extractUsername(String token){
        return extractClaims(token).getSubject();
    }
public boolean isTokenValid(String token,String email){
    Claims claims = extractClaims(token);
    return email.equals(claims.getSubject()) &&  claims.getExpiration().after(new Date());
}
private Claims extractClaims(String token){
        return Jwts.parser()
                .verifyWith(singingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
}
public Long getExpirationSeconds(){
        return expirationMs/1000;
}
}
