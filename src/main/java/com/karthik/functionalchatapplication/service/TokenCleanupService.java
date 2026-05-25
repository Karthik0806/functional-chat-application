package com.karthik.functionalchatapplication.service;

import com.karthik.functionalchatapplication.repo.RefreshTokenRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenCleanupService {

    private final RefreshTokenRepo refreshTokenRepo;

    @Scheduled(cron = "0 0 * * * *")
    public void cleanupExpiredTokens() {

        refreshTokenRepo.deleteAll(
                refreshTokenRepo.findAll()
                        .stream()
                        .filter(token ->
                                token.getExpiryDate()
                                        .isBefore(Instant.now())
                        )
                        .toList()
        );
    }
}
