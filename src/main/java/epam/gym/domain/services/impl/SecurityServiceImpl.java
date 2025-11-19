package epam.gym.domain.services.impl;

import epam.gym.domain.exceptions.ForbiddenException;
import epam.gym.domain.exceptions.UnauthorizedException;
import epam.gym.domain.models.TraineeModel;
import epam.gym.domain.models.TrainerModel;
import epam.gym.domain.services.interfaces.SecurityService;
import epam.gym.domain.services.interfaces.TraineeService;
import epam.gym.domain.services.interfaces.TrainerService;
import epam.gym.infrastructure.security.UserContext;
import epam.gym.infrastructure.security.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final UserContext userContext;
    private final TraineeService traineeService;
    private final TrainerService trainerService;

    @Override
    public void requireAuth() {
        if (!userContext.isAuthenticated()) {
            log.warn("Unauthorized access attempt");
            throw new UnauthorizedException("User is not authenticated");
        }
    }

    @Override
    public void requireRole(UserRole role) {
        if (!userContext.hasRole(role)) {
            log.warn("Access denied for user {}. Required role: {}, Actual role: {}",
                    userContext.getUsername(), role, userContext.getRole());
            throw new ForbiddenException("Access denied. Required role: " + role);
        }
    }

    @Override
    public void requireOwnership(String username) {
        String currentUsername = userContext.getUsername();
        if (!username.equals(currentUsername)) {
            log.warn("User {} attempted to access resource owned by {}",
                    currentUsername, username);
            throw new ForbiddenException("Access denied. You can only access your own resources");
        }
    }

    @Override
    public void requireTraineeOwnershipById(Long id) {
        TraineeModel model = traineeService.select(id);
        requireOwnership(model.getUsername());
    }

    @Override
    public void requireTrainerOwnershipById(Long id) {
        TrainerModel model = trainerService.select(id);
        requireOwnership(model.getUsername());
    }
}
