package epam.gym.domain.services;

import epam.gym.domain.dto.request.TraineeRequest;
import epam.gym.domain.dto.response.RegistrationResponse;
import epam.gym.domain.models.TraineeModel;
import epam.gym.domain.services.impl.TraineeServiceImpl;
import epam.gym.infrastructure.entities.Trainee;
import epam.gym.infrastructure.entities.User;
import epam.gym.infrastructure.mappers.TraineeMapper;
import epam.gym.infrastructure.repositories.TraineeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TraineeMapper traineeMapper;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private TraineeRequest traineeRequest;
    private TraineeModel traineeModel;
    private Trainee trainee;
    private User user;

    @BeforeEach
    void setUp() {
        traineeRequest = new TraineeRequest();
        traineeRequest.setFirstName("John");
        traineeRequest.setLastName("Doe");
        traineeRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        traineeRequest.setAddress("123 Main St");


        traineeModel = new TraineeModel();
        traineeModel.setId(1L);
        traineeModel.setFirstName("John");
        traineeModel.setLastName("Doe");
        traineeModel.setUsername("John.Doe");
        traineeModel.setPassword("password123");
        traineeModel.setIsActive(true);
        traineeModel.setDateOfBirth(LocalDate.of(1990, 1, 1));
        traineeModel.setAddress("123 Main St");

        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("John.Doe");
        user.setPassword("password123");
        user.setIsActive(true);

        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUser(user);
        trainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        trainee.setAddress("123 Main St");
    }

    @Test
    void testCreate_shouldCreateTraineeAndReturnCredentials() {
        when(traineeMapper.requestToModel(traineeRequest)).thenReturn(traineeModel);
        when(traineeMapper.toEntity(traineeModel)).thenReturn(trainee);
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        RegistrationResponse result = traineeService.create(traineeRequest);

        assertNotNull(result);
        assertEquals("John.Doe", result.getUsername());
        assertEquals("password123", result.getPassword());
        verify(traineeRepository).save(any(Trainee.class));
        verify(traineeMapper).requestToModel(traineeRequest);
        verify(traineeMapper).toEntity(traineeModel);
    }

    @Test
    void testUpdate_whenTraineeExists_shouldUpdateTrainee() {
        Long traineeId = 1L;
        traineeRequest.setFirstName("Jane");
        traineeRequest.setAddress("456 Oak Ave");

        when(traineeRepository.findById(traineeId)).thenReturn(trainee);
        when(traineeRepository.save(trainee)).thenReturn(trainee);
        when(traineeMapper.toModel(trainee)).thenReturn(traineeModel);

        TraineeModel result = traineeService.update(traineeRequest, traineeId);

        assertNotNull(result);
        assertEquals("Jane", trainee.getUser().getFirstName());
        assertEquals("456 Oak Ave", trainee.getAddress());
        verify(traineeRepository).findById(traineeId);
        verify(traineeRepository).save(trainee);
    }

    @Test
    void testUpdate_whenTraineeNotExists_shouldThrowException() {
        Long traineeId = 999L;
        when(traineeRepository.findById(traineeId)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () ->
            traineeService.update(traineeRequest, traineeId)
        );
        verify(traineeRepository).findById(traineeId);
        verify(traineeRepository, never()).save(any());
    }

    @Test
    void testSelect_whenTraineeExists_shouldReturnTrainee() {
        Long traineeId = 1L;
        when(traineeRepository.findById(traineeId)).thenReturn(trainee);
        when(traineeMapper.toModel(trainee)).thenReturn(traineeModel);

        TraineeModel result = traineeService.select(traineeId);

        assertNotNull(result);
        assertEquals(traineeId, result.getId());
        verify(traineeRepository).findById(traineeId);
        verify(traineeMapper).toModel(trainee);
    }

    @Test
    void testSelect_whenTraineeNotExists_shouldThrowException() {
        Long traineeId = 999L;
        when(traineeRepository.findById(traineeId)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () ->
            traineeService.select(traineeId)
        );
        verify(traineeRepository).findById(traineeId);
    }

    @Test
    void testDelete_shouldDeleteTrainee() {
        String username = "bla.bla";
        when(traineeRepository.findByUsername(username)).thenReturn(trainee);
        doNothing().when(traineeRepository).delete(trainee.getId());

        traineeService.delete(username);

        verify(traineeRepository).findByUsername(username);
        verify(traineeRepository).delete(trainee.getId());
    }

    @Test
    void testSetActiveStatus_shouldActivateTrainee() {
        String username = "John.Doe";
        user.setIsActive(false);
        when(traineeRepository.findByUsername(username)).thenReturn(trainee);
        doNothing().when(traineeRepository).activate(trainee.getId());

        traineeService.setActiveStatus(username, true);

        verify(traineeRepository).findByUsername(username);
        verify(traineeRepository).activate(trainee.getId());
    }

    @Test
    void testSetActiveStatus_shouldDeactivateTrainee() {
        String username = "John.Doe";
        user.setIsActive(true);
        when(traineeRepository.findByUsername(username)).thenReturn(trainee);
        doNothing().when(traineeRepository).deactivate(trainee.getId());

        traineeService.setActiveStatus(username, false);

        verify(traineeRepository).findByUsername(username);
        verify(traineeRepository).deactivate(trainee.getId());
    }

    @Test
    void testSetActiveStatus_whenAlreadyActive_shouldStillActivate() {
        String username = "John.Doe";
        user.setIsActive(true);
        when(traineeRepository.findByUsername(username)).thenReturn(trainee);
        doNothing().when(traineeRepository).activate(trainee.getId());

        traineeService.setActiveStatus(username, true);

        verify(traineeRepository).findByUsername(username);
        verify(traineeRepository).activate(trainee.getId());
    }

    @Test
    void testSetActiveStatus_whenAlreadyInactive_shouldStillDeactivate() {
        String username = "John.Doe";
        user.setIsActive(false);
        when(traineeRepository.findByUsername(username)).thenReturn(trainee);
        doNothing().when(traineeRepository).deactivate(trainee.getId());

        traineeService.setActiveStatus(username, false);

        verify(traineeRepository).findByUsername(username);
        verify(traineeRepository).deactivate(trainee.getId());
    }

    @Test
    void testSetActiveStatus_whenTraineeNotExists_shouldThrowException() {
        String username = "NonExistent.User";
        when(traineeRepository.findByUsername(username)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () ->
            traineeService.setActiveStatus(username, true)
        );
        verify(traineeRepository).findByUsername(username);
        verify(traineeRepository, never()).activate(any(Long.class));
        verify(traineeRepository, never()).deactivate(any(Long.class));
    }

    @Test
    void testChangePassword_whenTraineeExists_shouldChangePassword() {
        String trainerUsername = "Sushka";
        String oldPassword = "password123";
        String newPassword = "newPassword123";
        when(traineeRepository.authenticate(trainerUsername, oldPassword)).thenReturn(true);
        when(traineeRepository.findByUsername(trainerUsername)).thenReturn(trainee);
        doNothing().when(traineeRepository).changePassword(trainee.getId(), newPassword);

        traineeService.changePassword(trainerUsername, oldPassword, newPassword);

        verify(traineeRepository).authenticate(trainerUsername, oldPassword);
        verify(traineeRepository).findByUsername(trainerUsername);
        verify(traineeRepository).changePassword(trainee.getId(), newPassword);
    }

    @Test
    void testChangePassword_whenTraineeNotExists_shouldThrowException() {
        String trainerUsername = "Sushka.Bl";
        String oldPassword = "wrongPassword";
        String newPassword = "newPassword123";
        when(traineeRepository.authenticate(trainerUsername, oldPassword)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                traineeService.changePassword(trainerUsername, oldPassword, newPassword)
        );
        verify(traineeRepository).authenticate(trainerUsername, oldPassword);
        verify(traineeRepository, never()).findByUsername(any());
        verify(traineeRepository, never()).changePassword(any(Long.class), any(String.class));
    }
}
