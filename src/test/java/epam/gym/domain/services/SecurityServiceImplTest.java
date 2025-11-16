package epam.gym.domain.services;

import epam.gym.domain.exceptions.ForbiddenException;
import epam.gym.domain.exceptions.UnauthorizedException;
import epam.gym.domain.models.TraineeModel;
import epam.gym.domain.models.TrainerModel;
import epam.gym.domain.services.impl.SecurityServiceImpl;
import epam.gym.domain.services.interfaces.TraineeService;
import epam.gym.domain.services.interfaces.TrainerService;
import epam.gym.infrastructure.security.UserContext;
import epam.gym.infrastructure.security.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityServiceImplTest {

    @Mock
    private UserContext userContext;

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @InjectMocks
    private SecurityServiceImpl securityService;

    @Test
    void testRequireAuth_whenAuthenticated_shouldPass() {
        when(userContext.isAuthenticated()).thenReturn(true);

        assertDoesNotThrow(() -> securityService.requireAuth());
        verify(userContext).isAuthenticated();
    }

    @Test
    void testRequireAuth_whenNotAuthenticated_shouldThrowException() {
        when(userContext.isAuthenticated()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () ->
            securityService.requireAuth()
        );
        verify(userContext).isAuthenticated();
    }

    @Test
    void testRequireRole_whenUserHasRole_shouldPass() {
        when(userContext.hasRole(UserRole.TRAINEE)).thenReturn(true);

        assertDoesNotThrow(() -> securityService.requireRole(UserRole.TRAINEE));
        verify(userContext).hasRole(UserRole.TRAINEE);
    }

    @Test
    void testRequireRole_whenUserDoesNotHaveRole_shouldThrowException() {
        when(userContext.hasRole(UserRole.TRAINER)).thenReturn(false);
        when(userContext.getUsername()).thenReturn("John.Doe");
        when(userContext.getRole()).thenReturn(UserRole.TRAINEE);

        assertThrows(ForbiddenException.class, () ->
            securityService.requireRole(UserRole.TRAINER)
        );
        verify(userContext).hasRole(UserRole.TRAINER);
    }

    @Test
    void testRequireOwnership_whenUsernameMatches_shouldPass() {
        String username = "John.Doe";
        when(userContext.getUsername()).thenReturn(username);

        assertDoesNotThrow(() -> securityService.requireOwnership(username));
        verify(userContext).getUsername();
    }

    @Test
    void testRequireOwnership_whenUsernameDoesNotMatch_shouldThrowException() {
        String currentUsername = "John.Doe";
        String resourceUsername = "Jane.Smith";
        when(userContext.getUsername()).thenReturn(currentUsername);

        assertThrows(ForbiddenException.class, () ->
            securityService.requireOwnership(resourceUsername)
        );
        verify(userContext).getUsername();
    }

    @Test
    void testRequireTraineeOwnershipById_whenOwnerMatches_shouldPass() {
        Long traineeId = 1L;
        String username = "John.Doe";

        TraineeModel traineeModel = new TraineeModel();
        traineeModel.setId(traineeId);
        traineeModel.setUsername(username);

        when(traineeService.select(traineeId)).thenReturn(traineeModel);
        when(userContext.getUsername()).thenReturn(username);

        assertDoesNotThrow(() -> securityService.requireTraineeOwnershipById(traineeId));
        verify(traineeService).select(traineeId);
        verify(userContext).getUsername();
    }

    @Test
    void testRequireTraineeOwnershipById_whenOwnerDoesNotMatch_shouldThrowException() {
        Long traineeId = 1L;
        String traineeUsername = "John.Doe";
        String currentUsername = "Jane.Smith";

        TraineeModel traineeModel = new TraineeModel();
        traineeModel.setId(traineeId);
        traineeModel.setUsername(traineeUsername);

        when(traineeService.select(traineeId)).thenReturn(traineeModel);
        when(userContext.getUsername()).thenReturn(currentUsername);

        assertThrows(ForbiddenException.class, () ->
            securityService.requireTraineeOwnershipById(traineeId)
        );
        verify(traineeService).select(traineeId);
        verify(userContext).getUsername();
    }

    @Test
    void testRequireTrainerOwnershipById_whenOwnerMatches_shouldPass() {
        Long trainerId = 1L;
        String username = "Jane.Smith";

        TrainerModel trainerModel = new TrainerModel();
        trainerModel.setId(trainerId);
        trainerModel.setUsername(username);

        when(trainerService.select(trainerId)).thenReturn(trainerModel);
        when(userContext.getUsername()).thenReturn(username);

        assertDoesNotThrow(() -> securityService.requireTrainerOwnershipById(trainerId));
        verify(trainerService).select(trainerId);
        verify(userContext).getUsername();
    }

    @Test
    void testRequireTrainerOwnershipById_whenOwnerDoesNotMatch_shouldThrowException() {
        Long trainerId = 1L;
        String trainerUsername = "Jane.Smith";
        String currentUsername = "John.Doe";

        TrainerModel trainerModel = new TrainerModel();
        trainerModel.setId(trainerId);
        trainerModel.setUsername(trainerUsername);

        when(trainerService.select(trainerId)).thenReturn(trainerModel);
        when(userContext.getUsername()).thenReturn(currentUsername);

        assertThrows(ForbiddenException.class, () ->
            securityService.requireTrainerOwnershipById(trainerId)
        );
        verify(trainerService).select(trainerId);
        verify(userContext).getUsername();
    }
}
