package com.example.took_backend.global.security.jwt;

import com.example.took_backend.global.exception.ErrorCode;
import com.example.took_backend.global.exception.exceptionCollection.TokenExpirationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class TokenProvider {
    private final long ACCESS_TOKEN_EXPIRE_TIME = 3000;
    private final long REFRESH_TOKEN_EXPIRE_TIME = ACCESS_TOKEN_EXPIRE_TIME * 24 * 30 * 6;
    @Value("{spring.jwt.secret}")
    private String SECRET_KEY;

    @AllArgsConstructor
    private enum TokenType {
        ACCESS_TOKEN("accessToken"),
        REFRESH_TOKEN("refreshToken");
        String value;
    }

    @AllArgsConstructor
    private enum TokenClaimName {
        USER_EMAIL("userEmail"),
        TOKEN_TYPE("tokenType");
        String value;
    }

    // 암호화된 키 값 가져오기
    private Key getSignInKey(String secretKey) {
        byte[] bytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(bytes);
    }

    // 모든 Claims 추출 ( Payload에 들어가는 값은 Claims 이라고 부른다 )
    public Claims extractAllClaims(String token) {
        token = token.replace("Bearer ", "");
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey(SECRET_KEY))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new TokenExpirationException("The token has expired.", ErrorCode.TOKEN_EXPIRATION);
        }

    }

    // 토큰 값으로 유저 이메일 조회
    public String getUserEmail(String token) {
        return extractAllClaims(token).get(TokenClaimName.USER_EMAIL.value, String.class);
    }

    // 토큰 타입 확인
    public String getTokenType(String token) {
        return extractAllClaims(token).get(TokenClaimName.TOKEN_TYPE.value, String.class);
    }

    // Token 생성
    private String generateToken(String userEmail, TokenType tokenType, long expireTime) {
        final Claims claims = Jwts.claims();
        claims.put(TokenClaimName.USER_EMAIL.value, userEmail);
        claims.put(TokenClaimName.TOKEN_TYPE.value, tokenType);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(getSignInKey(SECRET_KEY), SignatureAlgorithm.HS256)
                .compact();
    }

    // AccessToken 토큰 생성

    public String generatedAccessToken(String email) {
        return generateToken(email, TokenType.ACCESS_TOKEN, ACCESS_TOKEN_EXPIRE_TIME);
    }

    // RefreshToken 토큰 생성
    public String generatedRefreshToken(String email) {
        return generateToken(email, TokenType.REFRESH_TOKEN, REFRESH_TOKEN_EXPIRE_TIME);
    }
}
