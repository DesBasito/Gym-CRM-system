package epam.gym.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import epam.gym.config.TestConfig;
import epam.gym.domain.dto.request.TrainerRequest;
import epam.gym.domain.dto.response.RegistrationResponse;
import epam.gym.infrastructure.controller.rest.TrainerControllerImpl;
import epam.gym.infrastructure.repositories.TrainerRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
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
        mockMvc = MockMvcBuilders.standaloneSetup(trainerControllerImpl).build();
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

        var trainer = trainerRepository.findByUsername(response.getUsername());
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
}
