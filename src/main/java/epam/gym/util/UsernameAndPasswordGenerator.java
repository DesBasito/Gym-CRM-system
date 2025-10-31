package epam.gym.util;

import epam.gym.dao.impl.UserDao;
import epam.gym.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class UsernameAndPasswordGenerator {
    private UserService userService;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+[]{}|;:,.<>?";
    @Value("password.length")
    private int length;

    @Autowired
    public void setUserDao(UserService userService) {
        this.userService = userService;
    }

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

    public boolean checkForDuplicate(String generatedUsername){
        return userService.isExists(generatedUsername);
    }
}
