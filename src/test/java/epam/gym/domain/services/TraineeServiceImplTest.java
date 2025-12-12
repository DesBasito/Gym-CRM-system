package epam.gym.domain.services;

import epam.gym.domain.dto.request.TraineeRequest;
import epam.gym.domain.dto.response.RegistrationResponse;
import epam.gym.domain.models.TraineeModel;
import epam.gym.domain.services.impl.TraineeServiceImpl;
import epam.gym.infrastructure.entities.Trainee;
import epam.gym.infrastructure.entities.Training;
import epam.gym.infrastructure.entities.User;
import epam.gym.infrastructure.mappers.TraineeMapper;
import epam.gym.infrastructure.repositories.TraineeRepository;
import epam.gym.security.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleService roleService;

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

        lenient().when(passwordEncoder.encode(anyString())).thenAnswer(invocation ->
            "encoded_" + invocation.getArgument(0));
        lenient().when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        lenient().doNothing().when(roleService).assignRoleToUser(any(), any());
    }

    @Test
    void testCreate_shouldCreateTraineeAndReturnCredentials() {
        when(traineeMapper.requestToModel(traineeRequest)).thenReturn(traineeModel);
        when(traineeMapper.toEntity(traineeModel)).thenReturn(trainee);
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        RegistrationResponse result = traineeService.create(traineeRequest);

        assertNotNull(result);
        assertEquals("John.Doe", result.getUsername());
        verify(passwordEncoder).encode(anyString());
        verify(traineeRepository).save(any(Trainee.class));
        verify(traineeMapper).requestToModel(traineeRequest);
        verify(traineeMapper).toEntity(traineeModel);
    }

    @Test
    void testUpdate_whenTraineeExists_shouldUpdateTrainee() {
        Long traineeId = 1L;
        traineeRequest.setFirstName("Jane");
        traineeRequest.setAddress("456 Oak Ave");

        when(traineeRepository.findById(traineeId)).thenReturn(Optional.of(trainee));
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
        when(traineeRepository.findById(traineeId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
            traineeService.update(traineeRequest, traineeId)
        );
        verify(traineeRepository).findById(traineeId);
        verify(traineeRepository, never()).save(any());
    }

    @Test
    void testSelect_whenTraineeExists_shouldReturnTrainee() {
        Long traineeId = 1L;
        when(traineeRepository.findById(traineeId)).thenReturn(Optional.of(trainee));
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
        when(traineeRepository.findById(traineeId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
            traineeService.select(traineeId)
        );
        verify(traineeRepository).findById(traineeId);
    }

    @Test
    void testDelete_shouldDeleteTraineeWithoutTrainings() {
        String username = "bla.bla";
        trainee.setTrainings(new LinkedHashSet<>());

        when(traineeRepository.findByUser_Username(username)).thenReturn(Optional.of(trainee));
        doNothing().when(traineeRepository).delete(trainee);

        traineeService.delete(username);

        verify(traineeRepository).findByUser_Username(username);
        verify(traineeRepository).delete(trainee);
        assertTrue(trainee.getTrainings().isEmpty());
    }

    @Test
    void testDelete_shouldDeleteTraineeAndClearTrainings() {
        String username = "John.Doe";

        Training training1 = new Training();
        training1.setId(1L);
        training1.setTrainingName("Morning Workout");

        Training training2 = new Training();
        training2.setId(2L);
        training2.setTrainingName("Evening Run");

        Set<Training> trainings = new LinkedHashSet<>();
        trainings.add(training1);
        trainings.add(training2);
        trainee.setTrainings(trainings);

        when(traineeRepository.findByUser_Username(username)).thenReturn(Optional.of(trainee));
        doNothing().when(traineeRepository).delete(trainee);

        traineeService.delete(username);

        verify(traineeRepository).findByUser_Username(username);
        verify(traineeRepository).delete(trainee);
        assertTrue(trainee.getTrainings().isEmpty(), "Trainings should be cleared before deletion");
    }

    @Test
    void testDelete_whenTraineeNotExists_shouldThrowNoSuchElementException() {
        String username = "NonExistent.User";
        when(traineeRepository.findByUser_Username(username)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
            traineeService.delete(username)
        );

        assertEquals("Trainee not found with username: " + username, exception.getMessage());
        verify(traineeRepository).findByUser_Username(username);
        verify(traineeRepository, never()).delete(any());
    }

    @Test
    void testSetActiveStatus_shouldActivateTrainee() {
        String username = "John.Doe";
        user.setIsActive(false);
        when(traineeRepository.findByUser_Username(username)).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        traineeService.setActiveStatus(username, true);

        verify(traineeRepository).findByUser_Username(username);
        verify(traineeRepository).save(trainee);
        assertTrue(trainee.getUser().getIsActive());
    }

    @Test
    void testSetActiveStatus_shouldDeactivateTrainee() {
        String username = "John.Doe";
        user.setIsActive(true);
        when(traineeRepository.findByUser_Username(username)).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        traineeService.setActiveStatus(username, false);

        verify(traineeRepository).findByUser_Username(username);
        verify(traineeRepository).save(trainee);
        assertFalse(trainee.getUser().getIsActive());
    }

    @Test
    void testSetActiveStatus_whenAlreadyActive_shouldStillActivate() {
        String username = "John.Doe";
        user.setIsActive(true);
        when(traineeRepository.findByUser_Username(username)).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        traineeService.setActiveStatus(username, true);

        verify(traineeRepository).findByUser_Username(username);
        verify(traineeRepository).save(trainee);
        assertTrue(trainee.getUser().getIsActive());
    }

    @Test
    void testSetActiveStatus_whenAlreadyInactive_shouldStillDeactivate() {
        String username = "John.Doe";
        user.setIsActive(false);
        when(traineeRepository.findByUser_Username(username)).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        traineeService.setActiveStatus(username, false);

        verify(traineeRepository).findByUser_Username(username);
        verify(traineeRepository).save(trainee);
        assertFalse(trainee.getUser().getIsActive());
    }

    @Test
    void testSetActiveStatus_whenTraineeNotExists_shouldThrowException() {
        String username = "NonExistent.User";
        when(traineeRepository.findByUser_Username(username)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
            traineeService.setActiveStatus(username, true)
        );
        verify(traineeRepository).findByUser_Username(username);
        verify(traineeRepository, never()).save(any());
    }

    @Test
    void testChangePassword_whenTraineeExists_shouldChangePassword() {
        String trainerUsername = "Sushka";
        String oldPassword = "password123";
        String newPassword = "newPassword123";
        when(traineeRepository.findByUser_Username(trainerUsername)).thenReturn(Optional.of(trainee));
        when(passwordEncoder.matches(eq(oldPassword), anyString())).thenReturn(true);
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        traineeService.changePassword(trainerUsername, oldPassword, newPassword);

        verify(traineeRepository).findByUser_Username(trainerUsername);
        verify(passwordEncoder).matches(eq(oldPassword), anyString());
        verify(passwordEncoder).encode(newPassword);
        verify(traineeRepository).save(trainee);
    }

    @Test
    void testChangePassword_whenTraineeNotExists_shouldThrowException() {
        String trainerUsername = "Sushka.Bl";
        String oldPassword = "wrongPassword";
        String newPassword = "newPassword123";
        when(traineeRepository.findByUser_Username(trainerUsername)).thenReturn(Optional.of(trainee));
        when(passwordEncoder.matches(eq(oldPassword), anyString())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                traineeService.changePassword(trainerUsername, oldPassword, newPassword)
        );
        verify(traineeRepository).findByUser_Username(trainerUsername);
        verify(passwordEncoder).matches(eq(oldPassword), anyString());
        verify(traineeRepository, never()).save(any());
    }
}
