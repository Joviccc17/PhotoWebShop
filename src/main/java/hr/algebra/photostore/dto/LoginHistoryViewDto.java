package hr.algebra.photostore.dto;

import hr.algebra.photostore.model.LoginHistory;

import java.time.LocalDateTime;

public record LoginHistoryViewDto(
        String userFirstName,
        String userLastName,
        String userEmail,
        LocalDateTime loginTime,
        String ipAddress
) {
    public static LoginHistoryViewDto from(LoginHistory h) {
        return new LoginHistoryViewDto(
                h.getUser().getFirstName(),
                h.getUser().getLastName(),
                h.getUser().getEmail(),
                h.getLoginTime(),
                h.getIpAddress());
    }
}