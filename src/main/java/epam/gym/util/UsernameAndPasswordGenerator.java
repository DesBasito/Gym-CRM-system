package epam.gym.util;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;

@UtilityClass
public class UsernameAndPasswordGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+[]{}|;:,.<>?";
    private static final int LENGTH = 10;

    public String generateAndGetUsername(String firstName, String lastName){
        return firstName + '.' + lastName;
    }

    public static String generateAndGetPassword(){
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(LENGTH);

        for (int i = 0; i < LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(randomIndex));
        }

        return password.toString();
    }

    public static void main(String[] args) {
        System.out.println(generateAndGetPassword());
    }
}
