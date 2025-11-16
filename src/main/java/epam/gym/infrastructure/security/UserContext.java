package epam.gym.infrastructure.security;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserContext {

    private static final ThreadLocal<UserSession> userHolder = new ThreadLocal<>();

    public void login(String username, UserRole role) {
        if (isAuthenticated()) {
            log.warn("User {} was already authenticated, performing automatic logout before new login", getUsername());
            logout();
        }
        UserSession session = new UserSession(username, role, true);
        userHolder.set(session);
        log.debug("User logged in: {} with role: {}", username, role);
    }

    public void logout() {
        UserSession session = userHolder.get();
        if (session != null) {
            log.debug("User logged out: {}", session.getUsername());
        }
        userHolder.remove();
    }

    public boolean isAuthenticated() {
        UserSession session = userHolder.get();
        return session != null && session.isAuthenticated();
    }

    public String getUsername() {
        UserSession session = userHolder.get();
        return session != null ? session.getUsername() : null;
    }

    public UserRole getRole() {
        UserSession session = userHolder.get();
        return session != null ? session.getRole() : null;
    }

    public boolean hasRole(UserRole role) {
        UserRole currentRole = getRole();
        return currentRole != null && currentRole.equals(role);
    }

    @Data
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    private static class UserSession {
        String username;
        UserRole role;
        boolean authenticated;
    }
}
