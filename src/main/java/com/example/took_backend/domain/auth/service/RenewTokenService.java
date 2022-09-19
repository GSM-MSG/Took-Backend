package com.example.took_backend.domain.auth.service;

import com.example.took_backend.domain.auth.RefreshTokenAuthEntity;
import com.example.took_backend.domain.auth.dto.response.NewTokenResponse;
import com.example.took_backend.domain.auth.exception.TokenNotFoundException;
import com.example.took_backend.domain.auth.repository.RefreshTokenRepository;
import com.example.took_backend.global.exception.exceptionCollection.TokenNotVaildException;
import com.example.took_backend.global.security.jwt.TokenProvider;
import com.example.took_backend.global.security.jwt.properties.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class RenewTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;
    private final JwtProperties jwtProperties;
    @Transactional
    public NewTokenResponse tokenReissuance(String reqToken) {
        String email = tokenProvider.getUserEmail(reqToken, jwtProperties.getRefreshSecret());
        RefreshTokenAuthEntity token = refreshTokenRepository.findById(email)
                .orElseThrow(() -> new TokenNotFoundException("토큰이 존재하지 없습니다."));
        if(!token.getToken().equals(reqToken)) {
            throw new TokenNotVaildException("토큰이 유효하지 않습니다");
        }
        String accessToken = tokenProvider.generatedAccessToken(email);
        String refreshToken = tokenProvider.generatedRefreshToken(email);
        ZonedDateTime expiredAt = tokenProvider.getExpiredAtToken(accessToken, jwtProperties.getAccessSecret());
        token.exchangeRefreshToken(refreshToken);
        refreshTokenRepository.save(token);
        return NewTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiredAt(expiredAt)
                .build();
    }
}
