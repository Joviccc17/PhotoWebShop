package hr.algebra.photostore.service;

import hr.algebra.photostore.exceptions.RefreshTokenException;
import hr.algebra.photostore.exceptions.ResourceNotFoundException;
import hr.algebra.photostore.model.RefreshToken;
import hr.algebra.photostore.repository.RefreshTokenRepository;
import hr.algebra.photostore.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public RefreshToken createRefreshToken(String username) {
        refreshTokenRepository.findByUserInfo_Email(username)
                .ifPresent(t -> refreshTokenRepository.deleteByToken(t.getToken()));

        RefreshToken refreshToken = RefreshToken.builder()
                .userInfo(userRepository.findByEmail(username)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "User not found: " + username)))
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(600000))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public void deleteRefreshToken(String token) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken(token);
        if (refreshToken.isPresent()) {
            refreshTokenRepository.delete(refreshToken.get());
        } else {
            throw new RefreshTokenException("Refresh token not found");
        }
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenException("Refresh token expired. Please log in again.");
        }
        return token;
    }

    public void deleteByUserName(String username) {
        refreshTokenRepository.deleteByUserInfo_Email(username);
    }
}