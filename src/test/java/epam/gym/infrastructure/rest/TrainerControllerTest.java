package epam.gym.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import epam.gym.domain.dto.request.ChangePasswordRequest;
import epam.gym.domain.dto.request.TrainerRequest;
import epam.gym.domain.dto.response.RegistrationResponse;
import epam.gym.infrastructure.controller.handler.GlobalExceptionHandler;
import epam.gym.infrastructure.controller.rest.TrainerControllerImpl;
import epam.gym.infrastructure.entities.Trainer;
import epam.gym.infrastructure.repositories.TrainerRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@WebAppConfiguration
@Transactional
class TrainerControllerTest {

    @Autowired
    private TrainerControllerImpl trainerControllerImpl;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private EntityManager entityManager;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(trainerControllerImpl)
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
    void testRegisterTrainer_shouldCreateTrainerAndReturnCredentials() throws Exception {
        TrainerRequest request = new TrainerRequest();
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setSpecialization("FITNESS");

        MvcResult result = mockMvc.perform(post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("Jane.Smith"))
                .andExpect(jsonPath("$.password").exists())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        RegistrationResponse response = objectMapper.readValue(responseBody, RegistrationResponse.class);

        assertNotNull(response.getUsername());
        assertNotNull(response.getPassword());
        assertEquals("Jane.Smith", response.getUsername());
        assertEquals(10, response.getPassword().length());

        var trainer = trainerRepository.findByUser_Username(response.getUsername()).orElseThrow();
        assertNotNull(trainer);
        assertEquals("Jane", trainer.getUser().getFirstName());
        assertEquals("Smith", trainer.getUser().getLastName());
        assertTrue(trainer.getUser().getIsActive());
        assertNotNull(trainer.getSpecialization());
        assertEquals("FITNESS", trainer.getSpecialization().getTrainingTypeName().name());
    }

    @Test
    void testRegisterTrainer_withDuplicateName_shouldGenerateUniqueUsername() throws Exception {
        TrainerRequest request1 = new TrainerRequest();
        request1.setFirstName("Jane");
        request1.setLastName("Smith");
        request1.setSpecialization("YOGA");

        mockMvc.perform(post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("Jane.Smith"));

        TrainerRequest request2 = new TrainerRequest();
        request2.setFirstName("Jane");
        request2.setLastName("Smith");
        request2.setSpecialization("FITNESS");

        MvcResult result = mockMvc.perform(post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("Jane.Smith1"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        RegistrationResponse response = objectMapper.readValue(responseBody, RegistrationResponse.class);

        assertEquals("Jane.Smith1", response.getUsername());
    }

    @Test
    void testChangePassword_withValidCredentials_shouldChangePassword() throws Exception {
        TrainerRequest trainerRequest = new TrainerRequest();
        trainerRequest.setFirstName("Mike");
        trainerRequest.setLastName("Johnson");
        trainerRequest.setSpecialization("CARDIO");

        MvcResult registerResult = mockMvc.perform(post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String registerResponseBody = registerResult.getResponse().getContentAsString();
        RegistrationResponse registrationResponse = objectMapper.readValue(registerResponseBody, RegistrationResponse.class);
        String username = registrationResponse.getUsername();
        String oldPassword = registrationResponse.getPassword();

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setUsername(username);
        changePasswordRequest.setOldPassword(oldPassword);
        changePasswordRequest.setNewPassword("newPassword456");

        mockMvc.perform(put("/api/v1/trainers/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isOk());

        var trainer = trainerRepository.findByUser_Username(username).orElseThrow();
        assertNotNull(trainer);
        assertThat(trainer.getUser().getPassword()).startsWith("$2a$10$");
    }

    @Test
    void testChangePassword_withInvalidOldPassword_shouldReturnError() throws Exception {
        TrainerRequest trainerRequest = new TrainerRequest();
        trainerRequest.setFirstName("Sarah");
        trainerRequest.setLastName("Williams");
        trainerRequest.setSpecialization("YOGA");

        MvcResult registerResult = mockMvc.perform(post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String registerResponseBody = registerResult.getResponse().getContentAsString();
        RegistrationResponse registrationResponse = objectMapper.readValue(registerResponseBody, RegistrationResponse.class);
        String username = registrationResponse.getUsername();

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setUsername(username);
        changePasswordRequest.setOldPassword("wrongPassword");
        changePasswordRequest.setNewPassword("newPassword789");

        mockMvc.perform(put("/api/v1/trainers/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testChangePassword_withNonExistentUser_shouldReturnError() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setUsername("NonExistent.Trainer");
        changePasswordRequest.setOldPassword("somePassword");
        changePasswordRequest.setNewPassword("newPassword999");

        mockMvc.perform(put("/api/v1/trainers/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testGetTrainerProfile_shouldReturnProfile() throws Exception {
        TrainerRequest request = new TrainerRequest();
        request.setFirstName("David");
        request.setLastName("Miller");
        request.setSpecialization("BOXING");

        MvcResult registerResult = mockMvc.perform(post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String registerResponseBody = registerResult.getResponse().getContentAsString();
        RegistrationResponse registrationResponse = objectMapper.readValue(registerResponseBody, RegistrationResponse.class);
        String username = registrationResponse.getUsername();

        mockMvc.perform(get("/api/v1/trainers/" + username))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.firstName").value("David"))
                .andExpect(jsonPath("$.lastName").value("Miller"))
                .andExpect(jsonPath("$.specialization").value("BOXING"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.trainees").isArray());
    }

    @Test
    void testGetTrainerProfile_withNonExistentUsername_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/trainers/NonExistent.Trainer"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testActivateDeactivateTrainer_shouldActivateTrainer() throws Exception {
        TrainerRequest request = new TrainerRequest();
        request.setFirstName("James");
        request.setLastName("Bond");
        request.setSpecialization("BOXING");

        MvcResult registerResult = mockMvc.perform(post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String registerResponseBody = registerResult.getResponse().getContentAsString();
        RegistrationResponse registrationResponse = objectMapper.readValue(registerResponseBody, RegistrationResponse.class);
        String username = registrationResponse.getUsername();

        mockMvc.perform(patch("/api/v1/trainers")
                        .param("username", username)
                        .param("isActive", "true"))
                .andExpect(status().isOk());

        Trainer trainer = trainerRepository.findByUser_Username(username).orElseThrow();
        assertNotNull(trainer);
        assertTrue(trainer.getUser().getIsActive());
    }

    @Test
    void testActivateDeactivateTrainer_shouldDeactivateTrainer() throws Exception {
        TrainerRequest request = new TrainerRequest();
        request.setFirstName("Tony");
        request.setLastName("Stark");
        request.setSpecialization("FITNESS");

        MvcResult registerResult = mockMvc.perform(post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String registerResponseBody = registerResult.getResponse().getContentAsString();
        RegistrationResponse registrationResponse = objectMapper.readValue(registerResponseBody, RegistrationResponse.class);
        String username = registrationResponse.getUsername();

        mockMvc.perform(patch("/api/v1/trainers")
                        .param("username", username)
                        .param("isActive", "false"))
                .andExpect(status().isOk());

        Trainer trainer = trainerRepository.findByUser_Username(username).orElseThrow();
        assertNotNull(trainer);
        assertFalse(trainer.getUser().getIsActive());
    }

    @Test
    void testActivateDeactivateTrainer_withNonExistentUsername_shouldReturnNotFound() throws Exception {
        mockMvc.perform(patch("/api/v1/trainers")
                        .param("username", "NonExistent.Trainer")
                        .param("isActive", "true"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testUpdateTrainerProfile_shouldUpdateSuccessfully() throws Exception {
        TrainerRequest request = new TrainerRequest();
        request.setFirstName("Bruce");
        request.setLastName("Wayne");
        request.setSpecialization("BOXING");

        MvcResult registerResult = mockMvc.perform(post("/api/v1/trainers")
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
                    "firstName": "Batman",
                    "lastName": "Wayne",
                    "specialization": "BOXING",
                    "isActive": false
                }
                """.formatted(username);

        mockMvc.perform(put("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.firstName").value("Batman"))
                .andExpect(jsonPath("$.lastName").value("Wayne"))
                .andExpect(jsonPath("$.specialization").value("BOXING"))
                .andExpect(jsonPath("$.isActive").value(false));

        Trainer trainer = trainerRepository.findByUser_Username(username).orElseThrow();
        assertNotNull(trainer);
        assertEquals("Batman", trainer.getUser().getFirstName());
        assertEquals("Wayne", trainer.getUser().getLastName());
        assertEquals("BOXING", trainer.getSpecialization().getTrainingTypeName().name());
        assertFalse(trainer.getUser().getIsActive());
    }
}
