package epam.gym.domain.services.interfaces;

import epam.gym.infrastructure.security.UserRole;

public interface SecurityService {
    void requireAuth();
    void requireRole(UserRole role);
    void requireOwnership(String username);
    void requireTraineeOwnershipById(Long id);
    void requireTrainerOwnershipById(Long id);
}
