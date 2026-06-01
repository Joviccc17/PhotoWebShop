package hr.algebra.photostore.listener;

import hr.algebra.photostore.model.LoginHistory;
import hr.algebra.photostore.repository.LoginHistoryRepository;
import hr.algebra.photostore.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class LoginSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final LoginHistoryRepository loginHistoryRepository;
    private final UserRepository userRepository;
    private final HttpServletRequest request;

    public LoginSuccessListener(LoginHistoryRepository loginHistoryRepository,
                                UserRepository userRepository,
                                HttpServletRequest request) {
        this.loginHistoryRepository = loginHistoryRepository;
        this.userRepository = userRepository;
        this.request = request;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        String email = event.getAuthentication().getName();

        userRepository.findByEmail(email).ifPresent(user -> {
            LoginHistory loginHistory = new LoginHistory();
            loginHistory.setUser(user);
            loginHistory.setIpAddress(getClientIpAddress());
            loginHistoryRepository.save(loginHistory);
        });
    }

    private String getClientIpAddress() {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}