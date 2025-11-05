package epam.gym.util;

import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Value;

import java.security.SecureRandom;

@UtilityClass
public class UsernameAndPasswordGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+[]{}|;:,.<>?";
    @Value("${password.length}")
    private int length;

    public String generateAndGetUsername(String firstName, String lastName){
        return firstName + '.' + lastName;
    }

    public String generateAndGetPassword(){
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(randomIndex));
        }

        return password.toString();
    }
}
