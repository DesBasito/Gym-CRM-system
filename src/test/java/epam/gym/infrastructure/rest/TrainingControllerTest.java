package epam.gym.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import epam.gym.domain.dto.request.TraineeRequest;
import epam.gym.domain.dto.request.TrainerRequest;
import epam.gym.domain.dto.response.RegistrationResponse;
import epam.gym.domain.services.interfaces.TraineeService;
import epam.gym.domain.services.interfaces.TrainerService;
import epam.gym.infrastructure.controller.handler.GlobalExceptionHandler;
import epam.gym.infrastructure.controller.rest.TrainingControllerImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@WebAppConfiguration
@Transactional
class TrainingControllerTest {

    @Autowired
    private TrainingControllerImpl trainingControllerImpl;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TraineeService traineeService;

    @Autowired
    private TrainerService trainerService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(trainingControllerImpl)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        entityManager.createNativeQuery("DELETE FROM trainings").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM trainers_trainees").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM trainees").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM trainers").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM users").executeUpdate();
        entityManager.flush();
    }

    @Test
    void testGetTrainingTypes_shouldReturnAllTrainingTypes() throws Exception {
        mockMvc.perform(get("/api/v1/trainings/types"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].trainingTypeName").exists());
    }

    @Test
    void testAddTraining_shouldCreateTrainingSuccessfully() throws Exception {
        TraineeRequest traineeRequest = new TraineeRequest();
        traineeRequest.setFirstName("John");
        traineeRequest.setLastName("Doe");
        RegistrationResponse traineeResponse = traineeService.create(traineeRequest);
        String traineeUsername = traineeResponse.getUsername();

        TrainerRequest trainerRequest = new TrainerRequest();
        trainerRequest.setFirstName("Jane");
        trainerRequest.setLastName("Smith");
        trainerRequest.setSpecialization("YOGA");
        RegistrationResponse trainerResponse = trainerService.create(trainerRequest);
        String trainerUsername = trainerResponse.getUsername();

        String trainingRequestJson = """
                {
                    "traineeUsername": "%s",
                    "trainerUsername": "%s",
                    "trainingName": "Morning Yoga Session",
                    "trainingType": "YOGA",
                    "trainingDate": "2024-01-15",
                    "trainingDuration": 60
                }
                """.formatted(traineeUsername, trainerUsername);

        mockMvc.perform(post("/api/v1/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(trainingRequestJson))
                .andExpect(status().isOk());
    }

    @Test
    void testGetTraineeTrainings_shouldReturnTrainings() throws Exception {
        TraineeRequest traineeRequest = new TraineeRequest();
        traineeRequest.setFirstName("John");
        traineeRequest.setLastName("Doe");
        RegistrationResponse traineeResponse = traineeService.create(traineeRequest);
        String traineeUsername = traineeResponse.getUsername();

        TrainerRequest trainerRequest = new TrainerRequest();
        trainerRequest.setFirstName("Jane");
        trainerRequest.setLastName("Smith");
        trainerRequest.setSpecialization("YOGA");
        RegistrationResponse trainerResponse = trainerService.create(trainerRequest);
        String trainerUsername = trainerResponse.getUsername();

        String trainingRequestJson = """
                {
                    "traineeUsername": "%s",
                    "trainerUsername": "%s",
                    "trainingName": "Morning Yoga Session",
                    "trainingType": "YOGA",
                    "trainingDate": "2024-01-15",
                    "trainingDuration": 60
                }
                """.formatted(traineeUsername, trainerUsername);

        mockMvc.perform(post("/api/v1/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(trainingRequestJson))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/trainings/trainee")
                        .param("username", traineeUsername))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].trainingName").value("Morning Yoga Session"))
                .andExpect(jsonPath("$[0].trainingType").exists())
                .andExpect(jsonPath("$[0].trainerName").exists());
    }

    @Test
    void testGetTrainerTrainings_shouldReturnTrainings() throws Exception {
        TraineeRequest traineeRequest = new TraineeRequest();
        traineeRequest.setFirstName("John");
        traineeRequest.setLastName("Doe");
        RegistrationResponse traineeResponse = traineeService.create(traineeRequest);
        String traineeUsername = traineeResponse.getUsername();

        TrainerRequest trainerRequest = new TrainerRequest();
        trainerRequest.setFirstName("Jane");
        trainerRequest.setLastName("Smith");
        trainerRequest.setSpecialization("YOGA");
        RegistrationResponse trainerResponse = trainerService.create(trainerRequest);
        String trainerUsername = trainerResponse.getUsername();

        String trainingRequestJson = """
                {
                    "traineeUsername": "%s",
                    "trainerUsername": "%s",
                    "trainingName": "Evening Yoga Session",
                    "trainingType": "YOGA",
                    "trainingDate": "2024-01-16",
                    "trainingDuration": 90
                }
                """.formatted(traineeUsername, trainerUsername);

        mockMvc.perform(post("/api/v1/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(trainingRequestJson))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/trainings/trainer")
                        .param("username", trainerUsername))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].trainingName").value("Evening Yoga Session"))
                .andExpect(jsonPath("$[0].trainingDuration").value(90));
    }
}