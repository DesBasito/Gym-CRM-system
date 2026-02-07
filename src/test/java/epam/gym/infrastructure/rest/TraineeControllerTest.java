package epam.gym.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import epam.gym.domain.dto.request.ChangePasswordRequest;
import epam.gym.domain.dto.request.TraineeRequest;
import epam.gym.domain.dto.response.RegistrationResponse;
import epam.gym.infrastructure.entities.Trainee;
import epam.gym.infrastructure.repositories.TraineeRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TraineeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        entityManager.createNativeQuery("DELETE FROM trainings").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM trainers_trainees").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM trainees WHERE user_id NOT IN (SELECT id FROM users WHERE username IN ('Alice.Brown', 'Bob.Wilson', 'Charlie.Davis'))").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM trainers WHERE user_id NOT IN (SELECT id FROM users WHERE username IN ('John.Doe', 'Jane.Smith', 'Mike.Johnson', 'Sarah.Connor'))").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM users WHERE username NOT IN ('John.Doe', 'Jane.Smith', 'Mike.Johnson', 'Alice.Brown', 'Bob.Wilson', 'Charlie.Davis', 'Sarah.Connor')").executeUpdate();
        entityManager.flush();
    }

    @Test
    void testRegisterTrainee_shouldCreateTraineeAndReturnCredentials() throws Exception {
        TraineeRequest request = new TraineeRequest();
        request.setFirstName("Test");
        request.setLastName("User");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        request.setAddress("123 Main St");

        MvcResult result = mockMvc.perform(post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("Test.User"))
                .andExpect(jsonPath("$.password").exists())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        RegistrationResponse response = objectMapper.readValue(responseBody, RegistrationResponse.class);

        assertNotNull(response.getUsername());
        assertNotNull(response.getPassword());
        assertEquals("Test.User", response.getUsername());
        assertEquals(10, response.getPassword().length());

        Trainee trainee = traineeRepository.findByUser_Username(response.getUsername()).orElseThrow();
        assertNotNull(trainee);
        assertEquals("Test", trainee.getUser().getFirstName());
        assertEquals("User", trainee.getUser().getLastName());
        assertTrue(trainee.getUser().getIsActive());
        assertEquals(LocalDate.of(1990, 1, 1), trainee.getDateOfBirth());
        assertEquals("123 Main St", trainee.getAddress());
    }

    @Test
    void testRegisterTrainee_withDuplicateName_shouldGenerateUniqueUsername() throws Exception {
        TraineeRequest request1 = new TraineeRequest();
        request1.setFirstName("Duplicate");
        request1.setLastName("User");

        mockMvc.perform(post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("Duplicate.User"));

        TraineeRequest request2 = new TraineeRequest();
        request2.setFirstName("Duplicate");
        request2.setLastName("User");

        MvcResult result = mockMvc.perform(post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("Duplicate.User1"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        RegistrationResponse response = objectMapper.readValue(responseBody, RegistrationResponse.class);

        assertEquals("Duplicate.User1", response.getUsername());
    }

    @Test
    @WithMockUser(username = "Alice.Brown", roles = {"TRAINEE"})
    void testChangePassword_withValidCredentials_shouldChangePassword() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOldPassword("qwe");
        changePasswordRequest.setNewPassword("newPassword123");

        mockMvc.perform(put("/api/v1/trainees/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isOk());

        Trainee trainee = traineeRepository.findByUser_Username("Alice.Brown").orElseThrow();
        assertNotNull(trainee);
        assertThat(trainee.getUser().getPassword()).startsWith("$2a$10$");
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
        changePasswordRequest.setOldPassword("somePassword");
        changePasswordRequest.setNewPassword("newPassword123");

        mockMvc.perform(put("/api/v1/trainees/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "Alice.Brown", roles = {"TRAINEE", "ADMIN"})
    void testGetTraineeProfile_shouldReturnProfile() throws Exception {
        mockMvc.perform(get("/api/v1/trainees/profile"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("Alice.Brown"))
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Brown"))
                .andExpect(jsonPath("$.dateOfBirth").exists())
                .andExpect(jsonPath("$.address").value("123 Main St"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.trainers").isArray());
    }

    @Test
    void testGetTraineeProfile_withNonExistentUsername_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/trainees/NonExistent.User"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteTraineeProfile_shouldDeleteSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/v1/trainees/Charlie.Davis"))
                .andExpect(status().isOk());

        assertTrue(traineeRepository.findByUser_Username("Charlie.Davis").isEmpty());
    }

    @Test
    void testDeleteTraineeProfile_withNonExistentUsername_shouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/trainees/NonExistent.User"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "Alice.Brown", roles = {"TRAINEE", "ADMIN"})
    void testGetAvailableTrainers_shouldReturnTrainersList() throws Exception {
        mockMvc.perform(get("/api/v1/trainees/available-trainers"))
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
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testActivateDeactivateTrainee_shouldActivateTrainee() throws Exception {
        mockMvc.perform(patch("/api/v1/trainees")
                        .param("username", "Bob.Wilson")
                        .param("isActive", "true"))
                .andExpect(status().isOk());

        Trainee trainee = traineeRepository.findByUser_Username("Bob.Wilson").orElseThrow();
        assertNotNull(trainee);
        assertTrue(trainee.getUser().getIsActive());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testActivateDeactivateTrainee_shouldDeactivateTrainee() throws Exception {
        mockMvc.perform(patch("/api/v1/trainees")
                        .param("username", "Alice.Brown")
                        .param("isActive", "false"))
                .andExpect(status().isOk());

        Trainee trainee = traineeRepository.findByUser_Username("Alice.Brown").orElseThrow();
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
    @WithMockUser(username = "Alice.Brown", roles = {"TRAINEE", "ADMIN"})
    void testUpdateTraineeProfile_shouldUpdateSuccessfully() throws Exception {
        String updateRequestJson = """
                {
                    "username": "Alice.Brown",
                    "firstName": "Alicia",
                    "lastName": "Brown",
                    "dateOfBirth": "1990-05-15",
                    "address": "456 New Address",
                    "isActive": true
                }
                """;

        mockMvc.perform(put("/api/v1/trainees/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("Alice.Brown"))
                .andExpect(jsonPath("$.firstName").value("Alicia"))
                .andExpect(jsonPath("$.lastName").value("Brown"))
                .andExpect(jsonPath("$.address").value("456 New Address"))
                .andExpect(jsonPath("$.isActive").value(true));

        Trainee trainee = traineeRepository.findByUser_Username("Alice.Brown").orElseThrow();
        assertNotNull(trainee);
        assertEquals("Alicia", trainee.getUser().getFirstName());
        assertEquals("Brown", trainee.getUser().getLastName());
        assertEquals("456 New Address", trainee.getAddress());
        assertTrue(trainee.getUser().getIsActive());
    }

    @Test
    @WithMockUser(username = "Alice.Brown", roles = {"TRAINEE"})
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
