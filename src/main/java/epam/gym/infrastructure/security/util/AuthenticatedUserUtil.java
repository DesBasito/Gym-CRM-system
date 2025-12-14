package epam.gym.infrastructure.security.util;

import epam.gym.infrastructure.entities.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserUtil {
    public boolean isProfileOwner(Long ownerId, String username) {
        if (username == null || ownerId == null) {
            return false;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return ownerId.equals(user.getId());
        }
        return false;
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
            !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getName();
        }
        throw new AccessDeniedException("U have no permission due to trouble with your authorization");
    }

    public boolean isProfileOwnerByUsername(String requestUsername, String authUsername) {
        if (requestUsername != null && authUsername != null) {
            return requestUsername.equals(authUsername);
        }
        return false;
    }
}
