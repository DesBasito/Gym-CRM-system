package epam.gym.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import epam.gym.domain.dto.request.ChangePasswordRequest;
import epam.gym.domain.dto.request.TraineeRequest;
import epam.gym.domain.dto.response.RegistrationResponse;
import epam.gym.infrastructure.controller.handler.GlobalExceptionHandler;
import epam.gym.infrastructure.controller.rest.TraineeControllerImpl;
import epam.gym.infrastructure.entities.Trainee;
import epam.gym.infrastructure.repositories.TraineeRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@WebAppConfiguration
@Transactional
class TraineeControllerTest {

    @Autowired
    private TraineeControllerImpl traineeControllerImpl;

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private EntityManager entityManager;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(traineeControllerImpl)
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
    void testRegisterTrainee_shouldCreateTraineeAndReturnCredentials() throws Exception {
        TraineeRequest request = new TraineeRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        request.setAddress("123 Main St");

        MvcResult result = mockMvc.perform(post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("John.Doe"))
                .andExpect(jsonPath("$.password").exists())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        RegistrationResponse response = objectMapper.readValue(responseBody, RegistrationResponse.class);

        assertNotNull(response.getUsername());
        assertNotNull(response.getPassword());
        assertEquals("John.Doe", response.getUsername());
        assertEquals(10, response.getPassword().length());

        Trainee trainee = traineeRepository.findByUser_Username(response.getUsername()).orElseThrow();
        assertNotNull(trainee);
        assertEquals("John", trainee.getUser().getFirstName());
        assertEquals("Doe", trainee.getUser().getLastName());
        assertTrue(trainee.getUser().getIsActive());
        assertEquals(LocalDate.of(1990, 1, 1), trainee.getDateOfBirth());
        assertEquals("123 Main St", trainee.getAddress());
    }

    @Test
    void testRegisterTrainee_withDuplicateName_shouldGenerateUniqueUsername() throws Exception {
        TraineeRequest request1 = new TraineeRequest();
        request1.setFirstName("John");
        request1.setLastName("Doe");

        mockMvc.perform(post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("John.Doe"));

        TraineeRequest request2 = new TraineeRequest();
        request2.setFirstName("John");
        request2.setLastName("Doe");

        MvcResult result = mockMvc.perform(post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("John.Doe1"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        RegistrationResponse response = objectMapper.readValue(responseBody, RegistrationResponse.class);

        assertEquals("John.Doe1", response.getUsername());
    }

    @Test
    void testChangePassword_withValidCredentials_shouldChangePassword() throws Exception {
        TraineeRequest traineeRequest = new TraineeRequest();
        traineeRequest.setFirstName("Jane");
        traineeRequest.setLastName("Smith");

        MvcResult registerResult = mockMvc.perform(post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(traineeRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String registerResponseBody = registerResult.getResponse().getContentAsString();
        RegistrationResponse registrationResponse = objectMapper.readValue(registerResponseBody, RegistrationResponse.class);
        String username = registrationResponse.getUsername();
        String oldPassword = registrationResponse.getPassword();

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setUsername(username);
        changePasswordRequest.setOldPassword(oldPassword);
        changePasswordRequest.setNewPassword("newPassword123");

        mockMvc.perform(put("/api/v1/trainees/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isOk());

        Trainee trainee = traineeRepository.findByUser_Username(username).orElseThrow();
        assertNotNull(trainee);
        assertEquals("newPassword123", trainee.getUser().getPassword());
    }

    @Test
    void testChangePassword_withInvalidOldPassword_shouldReturnError() throws Exception {
        TraineeRequest traineeRequest = new TraineeRequest();
        traineeRequest.setFirstName("Bob");
        traineeRequest.setLastName("Johnson");

        MvcResult registerResult = mockMvc.perform(post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(traineeRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String registerResponseBody = registerResult.getResponse().getContentAsString();
        RegistrationResponse registrationResponse = objectMapper.readValue(registerResponseBody, RegistrationResponse.class);
        String username = registrationResponse.getUsername();

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setUsername(username);
        changePasswordRequest.setOldPassword("wrongPassword");
        changePasswordRequest.setNewPassword("newPassword123");

        mockMvc.perform(put("/api/v1/trainees/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testChangePassword_withNonExistentUser_shouldReturnError() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setUsername("NonExistent.User");
        changePasswordRequest.setOldPassword("somePassword");
        changePasswordRequest.setNewPassword("newPassword123");

        mockMvc.perform(put("/api/v1/trainees/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testGetTraineeProfile_shouldReturnProfile() throws Exception {
        TraineeRequest request = new TraineeRequest();
        request.setFirstName("Alice");
        request.setLastName("Brown");
        request.setDateOfBirth(LocalDate.of(1995, 5, 15));
        request.setAddress("456 Oak Ave");

        MvcResult registerResult = mockMvc.perform(post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String registerResponseBody = registerResult.getResponse().getContentAsString();
        RegistrationResponse registrationResponse = objectMapper.readValue(registerResponseBody, RegistrationResponse.class);
        String username = registrationResponse.getUsername();

        mockMvc.perform(get("/api/v1/trainees/" + username))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Brown"))
                .andExpect(jsonPath("$.dateOfBirth").exists())
                .andExpect(jsonPath("$.address").value("456 Oak Ave"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.trainers").isArray());
    }

    @Test
    void testGetTraineeProfile_withNonExistentUsername_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/trainees/NonExistent.User"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testDeleteTraineeProfile_shouldDeleteSuccessfully() throws Exception {
        TraineeRequest request = new TraineeRequest();
        request.setFirstName("Tom");
        request.setLastName("Anderson");
        request.setDateOfBirth(LocalDate.of(1988, 8, 20));
        request.setAddress("789 Pine St");

        MvcResult registerResult = mockMvc.perform(post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String registerResponseBody = registerResult.getResponse().getContentAsString();
        RegistrationResponse registrationResponse = objectMapper.readValue(registerResponseBody, RegistrationResponse.class);
        String username = registrationResponse.getUsername();

        mockMvc.perform(delete("/api/v1/trainees/" + username))
                .andExpect(status().isOk());

        assertTrue(traineeRepository.findByUser_Username(username).isEmpty());
    }

    @Test
    void testDeleteTraineeProfile_withNonExistentUsername_shouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/trainees/NonExistent.User"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testGetAvailableTrainers_shouldReturnTrainersList() throws Exception {
        TraineeRequest traineeRequest = new TraineeRequest();
        traineeRequest.setFirstName("Emily");
        traineeRequest.setLastName("Davis");

        MvcResult registerResult = mockMvc.perform(post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(traineeRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String registerResponseBody = registerResult.getResponse().getContentAsString();
        RegistrationResponse registrationResponse = objectMapper.readValue(registerResponseBody, RegistrationResponse.class);
        String username = registrationResponse.getUsername();

        mockMvc.perform(get("/api/v1/trainees/" + username + "/available-trainers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetAvailableTrainers_withNonExistentTrainee_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/trainees/NonExistent.User/available-trainers"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testActivateDeactivateTrainee_shouldActivateTrainee() throws Exception {
        TraineeRequest request = new TraineeRequest();
        request.setFirstName("Michael");
        request.setLastName("Scott");

        MvcResult registerResult = mockMvc.perform(post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String registerResponseBody = registerResult.getResponse().getContentAsString();
        RegistrationResponse registrationResponse = objectMapper.readValue(registerResponseBody, RegistrationResponse.class);
        String username = registrationResponse.getUsername();

        mockMvc.perform(patch("/api/v1/trainees")
                        .param("username", username)
                        .param("isActive", "true"))
                .andExpect(status().isOk());

        Trainee trainee = traineeRepository.findByUser_Username(username).orElseThrow();
        assertNotNull(trainee);
        assertTrue(trainee.getUser().getIsActive());
    }

    @Test
    void testActivateDeactivateTrainee_shouldDeactivateTrainee() throws Exception {
        TraineeRequest request = new TraineeRequest();
        request.setFirstName("Dwight");
        request.setLastName("Schrute");

        MvcResult registerResult = mockMvc.perform(post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String registerResponseBody = registerResult.getResponse().getContentAsString();
        RegistrationResponse registrationResponse = objectMapper.readValue(registerResponseBody, RegistrationResponse.class);
        String username = registrationResponse.getUsername();

        mockMvc.perform(patch("/api/v1/trainees")
                        .param("username", username)
                        .param("isActive", "false"))
                .andExpect(status().isOk());

        Trainee trainee = traineeRepository.findByUser_Username(username).orElseThrow();
        assertNotNull(trainee);
        assertFalse(trainee.getUser().getIsActive());
    }

    @Test
    void testActivateDeactivateTrainee_withNonExistentUsername_shouldReturnNotFound() throws Exception {
        mockMvc.perform(patch("/api/v1/trainees")
                        .param("username", "NonExistent.User")
                        .param("isActive", "true"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testUpdateTraineeProfile_shouldUpdateSuccessfully() throws Exception {
        TraineeRequest request = new TraineeRequest();
        request.setFirstName("Jim");
        request.setLastName("Halpert");
        request.setDateOfBirth(LocalDate.of(1985, 10, 1));
        request.setAddress("123 Scranton St");

        MvcResult registerResult = mockMvc.perform(post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String registerResponseBody = registerResult.getResponse().getContentAsString();
        RegistrationResponse registrationResponse = objectMapper.readValue(registerResponseBody, RegistrationResponse.class);
        String username = registrationResponse.getUsername();

        String updateRequestJson = """
                {
                    "username": "%s",
                    "firstName": "James",
                    "lastName": "Halpert",
                    "dateOfBirth": "1985-10-01",
                    "address": "456 Dunder Mifflin Ave",
                    "isActive": false
                }
                """.formatted(username);

        mockMvc.perform(put("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.firstName").value("James"))
                .andExpect(jsonPath("$.lastName").value("Halpert"))
                .andExpect(jsonPath("$.address").value("456 Dunder Mifflin Ave"))
                .andExpect(jsonPath("$.isActive").value(false));

        Trainee trainee = traineeRepository.findByUser_Username(username).orElseThrow();
        assertNotNull(trainee);
        assertEquals("James", trainee.getUser().getFirstName());
        assertEquals("Halpert", trainee.getUser().getLastName());
        assertEquals("456 Dunder Mifflin Ave", trainee.getAddress());
        assertFalse(trainee.getUser().getIsActive());
    }

    @Test
    void testUpdateTrainersList_shouldUpdateSuccessfully() throws Exception {
        TraineeRequest traineeRequest = new TraineeRequest();
        traineeRequest.setFirstName("Pam");
        traineeRequest.setLastName("Beesly");

        MvcResult traineeResult = mockMvc.perform(post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(traineeRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String traineeResponseBody = traineeResult.getResponse().getContentAsString();
        RegistrationResponse traineeResponse = objectMapper.readValue(traineeResponseBody, RegistrationResponse.class);
        String traineeUsername = traineeResponse.getUsername();

        String updateTrainersRequestJson = """
                {
                    "traineeUsername": "%s",
                    "trainerUsernames": []
                }
                """.formatted(traineeUsername);

        mockMvc.perform(put("/api/v1/trainees/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateTrainersRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }
}
