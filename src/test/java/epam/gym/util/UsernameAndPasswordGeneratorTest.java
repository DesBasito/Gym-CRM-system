package epam.gym.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsernameAndPasswordGeneratorTest {

    @Test
    void testGenerateAndGetUsername_shouldConcatenateWithDot() {
        String firstName = "John";
        String lastName = "Doe";
        String username = UsernameAndPasswordGenerator.generateAndGetUsername(firstName, lastName);

        assertNotNull(username);
        assertEquals("John.Doe", username);
    }

    @Test
    void testGenerateAndGetUsername_withSingleCharNames_shouldWork() {
        String firstName = "A";
        String lastName = "B";
        String username = UsernameAndPasswordGenerator.generateAndGetUsername(firstName, lastName);

        assertEquals("A.B", username);
    }

    @Test
    void testGenerateAndGetUsername_withSpecialCharacters_shouldWork() {
        String firstName = "Jean-Pierre";
        String lastName = "O'Brien";
        String username = UsernameAndPasswordGenerator.generateAndGetUsername(firstName, lastName);

        assertEquals("Jean-Pierre.O'Brien", username);
    }

    @Test
    void testGenerateAndGetPassword_shouldGeneratePasswordOfLength10() {
        String password = UsernameAndPasswordGenerator.generateAndGetPassword();

        assertNotNull(password);
        assertEquals(10, password.length());
    }

    @Test
    void testGenerateAndGetPassword_shouldContainValidCharacters() {
        String password = UsernameAndPasswordGenerator.generateAndGetPassword();
        String validCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+[]{}|;:,.<>?";

        assertNotNull(password);
        for (char c : password.toCharArray()) {
            assertTrue(validCharacters.indexOf(c) >= 0,
                    "Password contains invalid character: " + c);
        }
    }

    @Test
    void testGenerateAndGetPassword_shouldGenerateDifferentPasswords() {
        String password1 = UsernameAndPasswordGenerator.generateAndGetPassword();
        String password2 = UsernameAndPasswordGenerator.generateAndGetPassword();
        String password3 = UsernameAndPasswordGenerator.generateAndGetPassword();

        assertFalse(password1.equals(password2) && password2.equals(password3),
                "Generated passwords should be different");
    }

    @Test
    void testGenerateAndGetPassword_shouldBeSecureRandom() {
        String password1 = UsernameAndPasswordGenerator.generateAndGetPassword();
        String password2 = UsernameAndPasswordGenerator.generateAndGetPassword();

        assertNotEquals(password1, password2);
        assertNotNull(password1);
        assertNotNull(password2);
    }
}
