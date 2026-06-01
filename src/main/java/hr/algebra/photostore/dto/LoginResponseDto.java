package hr.algebra.photostore.dto;

public record LoginResponseDto(
        String accessToken,
        String refreshToken,
        String email,
        String role
) {}