package com.example.demo.service;

import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.User;
import com.example.demo.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    private static final SecureRandom random = new SecureRandom();

    public String createRefreshToken(User user) {
        byte[] bytes = new byte[64];
        random.nextBytes(bytes);

        String rawToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setTokenHash(sha256(rawToken));
        token.setCreatedAt(Instant.now());
        token.setExpiresAt(Instant.now().plusSeconds(7 * 24 * 60 * 60));
        token.setRevoked(false);
        repository.save(token);
        return rawToken;
    }

    public void logout(String refreshToken) {
        String hash = RefreshTokenService.sha256(refreshToken);
        repository.findByTokenHash(hash)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    repository.save(token);
                });
    }

    public static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash value", e);
        }
    }

    public Optional<RefreshToken> findByTokenHash(String hash) {
        return repository.findByTokenHash(hash);
    }

}
