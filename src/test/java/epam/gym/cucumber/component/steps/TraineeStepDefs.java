package epam.gym.cucumber.component.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import epam.gym.domain.dto.request.TraineeRequest;
import epam.gym.domain.dto.request.UpdateTraineeTrainersRequest;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class TraineeStepDefs {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SharedTestContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @When("I register a trainee with firstName {string} lastName {string} dateOfBirth {string} address {string}")
    public void iRegisterTrainee(String firstName, String lastName, String dateOfBirth, String address) throws Exception {
        TraineeRequest request = new TraineeRequest();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        if (dateOfBirth != null && !dateOfBirth.isBlank()) {
            request.setDateOfBirth(LocalDate.parse(dateOfBirth));
        }
        request.setAddress(address);

        MvcResult result = mockMvc.perform(
                post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andReturn();

        context.setLastResult(result);
    }

    @Then("the response contains a username {string}")
    public void theResponseContainsUsername(String expectedUsername) throws Exception {
        assertThat(context.getLastResponseBody()).contains(expectedUsername);
    }

    @Then("the response contains a generated password")
    public void theResponseContainsAGeneratedPassword() throws Exception {
        assertThat(context.getLastResponseBody()).contains("password");
    }

    @When("I PATCH {string} with param {string} {string} and param {string} {string}")
    public void iPatchWithParams(String path, String param1, String value1, String param2, String value2) throws Exception {
        MockHttpServletRequestBuilder builder = patch(path)
                .param(param1, value1)
                .param(param2, value2);
        if (context.isAuthenticated()) {
            builder = (MockHttpServletRequestBuilder) builder.with(context.getSecurityProcessor());
        }
        MvcResult result = mockMvc.perform(builder).andReturn();
        context.setLastResult(result);
    }

    @When("I update trainers list with trainer usernames {string}")
    public void iUpdateTrainersList(String trainerUsernames) throws Exception {
        List<String> usernames = Arrays.asList(trainerUsernames.split(","));
        UpdateTraineeTrainersRequest request = new UpdateTraineeTrainersRequest();
        request.setTrainerUsernames(usernames);

        MockHttpServletRequestBuilder builder = put("/api/v1/trainees/trainers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));
        if (context.isAuthenticated()) {
            builder = (MockHttpServletRequestBuilder) builder.with(context.getSecurityProcessor());
        }
        MvcResult result = mockMvc.perform(builder).andReturn();
        context.setLastResult(result);
    }
}
