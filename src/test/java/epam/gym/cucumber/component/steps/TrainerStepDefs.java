package epam.gym.cucumber.component.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import epam.gym.domain.dto.request.TrainerRequest;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class TrainerStepDefs {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SharedTestContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @When("I register a trainer with firstName {string} lastName {string} specialization {string}")
    public void iRegisterTrainer(String firstName, String lastName, String specialization) throws Exception {
        TrainerRequest request = new TrainerRequest();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setSpecialization(specialization);

        MvcResult result = mockMvc.perform(
                post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andReturn();

        context.setLastResult(result);
    }
}
