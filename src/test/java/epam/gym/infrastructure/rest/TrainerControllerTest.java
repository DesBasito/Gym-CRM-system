package epam.gym.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import epam.gym.domain.dto.request.ChangePasswordRequest;
import epam.gym.domain.dto.request.TrainerRequest;
import epam.gym.domain.dto.response.RegistrationResponse;
import epam.gym.infrastructure.entities.Trainer;
import epam.gym.infrastructure.repositories.TrainerRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TrainerRepository trainerRepository;

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
    void testRegisterTrainer_shouldCreateTrainerAndReturnCredentials() throws Exception {
        TrainerRequest request = new TrainerRequest();
        request.setFirstName("Test");
        request.setLastName("Trainer");
        request.setSpecialization("FITNESS");

        MvcResult result = mockMvc.perform(post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("Test.Trainer"))
                .andExpect(jsonPath("$.password").exists())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        RegistrationResponse response = objectMapper.readValue(responseBody, RegistrationResponse.class);

        assertNotNull(response.getUsername());
        assertNotNull(response.getPassword());
        assertEquals("Test.Trainer", response.getUsername());
        assertEquals(10, response.getPassword().length());

        var trainer = trainerRepository.findByUser_Username(response.getUsername()).orElseThrow();
        assertNotNull(trainer);
        assertEquals("Test", trainer.getUser().getFirstName());
        assertEquals("Trainer", trainer.getUser().getLastName());
        assertTrue(trainer.getUser().getIsActive());
        assertNotNull(trainer.getSpecialization());
        assertEquals("FITNESS", trainer.getSpecialization().getTrainingTypeName().name());
    }

    @Test
    void testRegisterTrainer_withDuplicateName_shouldGenerateUniqueUsername() throws Exception {
        TrainerRequest request1 = new TrainerRequest();
        request1.setFirstName("Duplicate");
        request1.setLastName("Trainer");
        request1.setSpecialization("YOGA");

        mockMvc.perform(post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("Duplicate.Trainer"));

        TrainerRequest request2 = new TrainerRequest();
        request2.setFirstName("Duplicate");
        request2.setLastName("Trainer");
        request2.setSpecialization("FITNESS");

        MvcResult result = mockMvc.perform(post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("Duplicate.Trainer1"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        RegistrationResponse response = objectMapper.readValue(responseBody, RegistrationResponse.class);

        assertEquals("Duplicate.Trainer1", response.getUsername());
    }

    @Test
    @WithMockUser(username = "Sarah.Connor", roles = {"TRAINER"})
    void testChangePassword_withValidCredentials_shouldChangePassword() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOldPassword("qwe");
        changePasswordRequest.setNewPassword("newPassword456");

        mockMvc.perform(put("/api/v1/trainers/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isOk());

        var trainer = trainerRepository.findByUser_Username("Sarah.Connor").orElseThrow();
        assertNotNull(trainer);
        assertThat(trainer.getUser().getPassword()).startsWith("$2a$10$");
    }

    @Test
    @WithMockUser(username = "Sarah.Connor", roles = {"TRAINER"})
    void testChangePassword_withInvalidOldPassword_shouldReturnError() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOldPassword("wrongPassword");
        changePasswordRequest.setNewPassword("qwe2");

        mockMvc.perform(put("/api/v1/trainers/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testChangePassword_withNonExistentUser_shouldReturnError() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOldPassword("somePassword");
        changePasswordRequest.setNewPassword("newPassword999");

        mockMvc.perform(put("/api/v1/trainers/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "Sarah.Connor", roles = {"TRAINER"})
    void testGetTrainerProfile_shouldReturnProfile() throws Exception {
        mockMvc.perform(get("/api/v1/trainers/profile"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("Sarah.Connor"))
                .andExpect(jsonPath("$.firstName").value("Sarah"))
                .andExpect(jsonPath("$.lastName").value("Connor"))
                .andExpect(jsonPath("$.specialization").exists())
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.trainees").isArray());
    }

    @Test
    void testGetTrainerProfile_withNonExistentUsername_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/trainers/NonExistent.Trainer"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testActivateDeactivateTrainer_shouldActivateTrainer() throws Exception {
        mockMvc.perform(patch("/api/v1/trainers")
                        .param("username", "John.Doe")
                        .param("isActive", "true"))
                .andExpect(status().isOk());

        Trainer trainer = trainerRepository.findByUser_Username("John.Doe").orElseThrow();
        assertNotNull(trainer);
        assertTrue(trainer.getUser().getIsActive());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testActivateDeactivateTrainer_shouldDeactivateTrainer() throws Exception {
        mockMvc.perform(patch("/api/v1/trainers")
                        .param("username", "Sarah.Connor")
                        .param("isActive", "false"))
                .andExpect(status().isOk());

        Trainer trainer = trainerRepository.findByUser_Username("Sarah.Connor").orElseThrow();
        assertNotNull(trainer);
        assertFalse(trainer.getUser().getIsActive());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testActivateDeactivateTrainer_withNonExistentUsername_shouldReturnNotFound() throws Exception {
        mockMvc.perform(patch("/api/v1/trainers")
                        .param("username", "NonExistent.Trainer")
                        .param("isActive", "true"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "Sarah.Connor", roles = {"TRAINER", "ADMIN"})
    void testUpdateTrainerProfile_shouldUpdateSuccessfully() throws Exception {
        String updateRequestJson = """
                {
                    "username": "Sarah.Connor",
                    "firstName": "Sara",
                    "lastName": "Connor",
                    "specialization": "FITNESS",
                    "isActive": true
                }
                """;

        mockMvc.perform(put("/api/v1/trainers/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("Sarah.Connor"))
                .andExpect(jsonPath("$.firstName").value("Sara"))
                .andExpect(jsonPath("$.lastName").value("Connor"))
                .andExpect(jsonPath("$.specialization").value("FITNESS"))
                .andExpect(jsonPath("$.isActive").value(true));

        Trainer trainer = trainerRepository.findByUser_Username("Sarah.Connor").orElseThrow();
        assertNotNull(trainer);
        assertEquals("Sara", trainer.getUser().getFirstName());
        assertEquals("Connor", trainer.getUser().getLastName());
        assertEquals("FITNESS", trainer.getSpecialization().getTrainingTypeName().name());
        assertTrue(trainer.getUser().getIsActive());
    }
}
