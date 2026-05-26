package com.karthik.functionalchatapplication.service;

import com.karthik.functionalchatapplication.entity.RefreshToken;
import com.karthik.functionalchatapplication.dto.AuthResponse;
import com.karthik.functionalchatapplication.dto.LoginRequest;
import com.karthik.functionalchatapplication.dto.RefreshRequest;
import com.karthik.functionalchatapplication.dto.RegisterRequest;

import com.karthik.functionalchatapplication.entity.User;

import com.karthik.functionalchatapplication.exception.UserAlreadyExistsException;
import com.karthik.functionalchatapplication.repo.RefreshTokenRepo;
import com.karthik.functionalchatapplication.repo.UserRepo;
import com.karthik.functionalchatapplication.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepo refreshTokenRepo;
    @Transactional
    public AuthResponse login(LoginRequest request) {

        log.info("Login attempt for user: {}", request.getUsername());

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        refreshTokenRepo.deleteByUsername(request.getUsername());
        String accessToken = jwtService.generateToken(request.getUsername());
        String refreshTokenValue = jwtService.generateRefreshToken();


        RefreshToken refreshToken = RefreshToken.builder()
                        .token(refreshTokenValue)
                        .username(request.getUsername())
                        .expiryDate(Instant.now().plus(7, ChronoUnit.DAYS))
                        .build();
        refreshTokenRepo.save(refreshToken);

        log.info("Login successful for user: {}", request.getUsername());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .build();
    }
    public void logout(String refreshToken) {

        refreshTokenRepo.findByToken(refreshToken).ifPresent(refreshTokenRepo::delete);
    }

    public String register(RegisterRequest request) {

        log.info("Registration attempt for user: {}", request.getUsername());

        if (userRepo.findByUsername(request.getUsername()).isPresent()) {

            throw new UserAlreadyExistsException("Username already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepo.save(user);

        log.info("User registered successfully: {}", request.getUsername());
        return "User registered successfully";
    }

    public AuthResponse refreshToken(RefreshRequest request) {

        RefreshToken refreshToken = refreshTokenRepo.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        String newAccessToken = jwtService.generateToken(refreshToken.getUsername());

        String newRefresh = jwtService.generateRefreshToken();
        RefreshToken newRefreshToken = RefreshToken.builder()
                .token(newRefresh)
                .username(refreshToken.getUsername())
                .expiryDate(Instant.now().plus(7, ChronoUnit.DAYS))
                .build();
        refreshTokenRepo.save(newRefreshToken);
        log.info("Access token refreshed for user: {}", refreshToken.getUsername());
        refreshTokenRepo.delete(refreshToken);



        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefresh)
                .build();
    }
}