package epam.gym.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsernameGenerator {
    private final UserDao traineeDao;

    public String generateAndGetUsername(String firstName, String lastName){

    }

    private boolean checkForDuplicate(String generatedUsername){
        tra
    }
}
