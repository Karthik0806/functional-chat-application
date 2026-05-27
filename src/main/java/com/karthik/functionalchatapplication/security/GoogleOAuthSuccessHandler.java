package com.karthik.functionalchatapplication.security;

import com.karthik.functionalchatapplication.Enumeration.AuthProvider;
import com.karthik.functionalchatapplication.entity.RefreshToken;
import com.karthik.functionalchatapplication.entity.User;
import com.karthik.functionalchatapplication.repo.RefreshTokenRepo;
import com.karthik.functionalchatapplication.repo.UserRepo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class GoogleOAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepo userRepo;
    private final RefreshTokenRepo refreshTokenRepo;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");

        User user = userRepo.findByUsername(email)
                .orElseGet(() -> {

                    User newUser = User.builder()
                            .username(email)
                            .password("")
                            .provider(AuthProvider.GOOGLE)
                            .build();

                    return userRepo.save(newUser);
                });

        refreshTokenRepo.deleteByUsername(user.getUsername());

        String accessToken = jwtService.generateToken(user.getUsername());

        String refreshValue = jwtService.generateRefreshToken();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshValue)
                .username(user.getUsername())
                .expiryDate(Instant.now().plus(7, ChronoUnit.DAYS))
                .build();

        refreshTokenRepo.save(refreshToken);

        String frontendUrl = "https://chat.karthiknarravula.dev";

        String redirectUrl =
                frontendUrl
                        + "/oauth2/callback"
                        + "?accessToken=" + URLEncoder.encode(accessToken, StandardCharsets.UTF_8)
                        + "&refreshToken=" + URLEncoder.encode(refreshValue, StandardCharsets.UTF_8)
                        + "&username=" + URLEncoder.encode(user.getUsername(), StandardCharsets.UTF_8);

        response.sendRedirect(redirectUrl);
    }
}