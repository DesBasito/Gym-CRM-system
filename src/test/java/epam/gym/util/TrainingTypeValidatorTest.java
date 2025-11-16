package epam.gym.util;

import epam.gym.constants.TrainingType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrainingTypeValidatorTest {

    @Test
    void testIsValid_withValidType_shouldReturnTrue() {
        assertTrue(TrainingTypeValidator.isValid("FITNESS"));
        assertTrue(TrainingTypeValidator.isValid("YOGA"));
        assertTrue(TrainingTypeValidator.isValid("CARDIO"));
        assertTrue(TrainingTypeValidator.isValid("BOXING"));
        assertTrue(TrainingTypeValidator.isValid("PILATES"));
        assertTrue(TrainingTypeValidator.isValid("CROSSFIT"));
        assertTrue(TrainingTypeValidator.isValid("SWIMMING"));
    }

    @Test
    void testIsValid_withInvalidType_shouldReturnFalse() {
        assertFalse(TrainingTypeValidator.isValid("INVALID"));
        assertFalse(TrainingTypeValidator.isValid("RUNNING"));
        assertFalse(TrainingTypeValidator.isValid("BASKETBALL"));
    }

    @Test
    void testIsValid_withNull_shouldReturnFalse() {
        assertFalse(TrainingTypeValidator.isValid(null));
    }

    @Test
    void testIsValid_withEmptyString_shouldReturnFalse() {
        assertFalse(TrainingTypeValidator.isValid(""));
    }

    @Test
    void testIsValid_withLowerCase_shouldWork() {
        assertTrue(TrainingTypeValidator.isValid("fitness"));
        assertTrue(TrainingTypeValidator.isValid("yoga"));
    }

    @Test
    void testParse_withValidType_shouldReturnEnum() {
        assertEquals(TrainingType.FITNESS, TrainingTypeValidator.parse("FITNESS"));
        assertEquals(TrainingType.YOGA, TrainingTypeValidator.parse("YOGA"));
        assertEquals(TrainingType.CARDIO, TrainingTypeValidator.parse("CARDIO"));
    }

    @Test
    void testParse_withLowerCase_shouldReturnEnum() {
        assertEquals(TrainingType.FITNESS, TrainingTypeValidator.parse("fitness"));
        assertEquals(TrainingType.YOGA, TrainingTypeValidator.parse("yoga"));
    }

    @Test
    void testParse_withMixedCase_shouldReturnEnum() {
        assertEquals(TrainingType.FITNESS, TrainingTypeValidator.parse("FiTnEsS"));
        assertEquals(TrainingType.YOGA, TrainingTypeValidator.parse("YoGa"));
    }

    @Test
    void testParse_withInvalidType_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> TrainingTypeValidator.parse("INVALID")
        );
        assertTrue(exception.getMessage().contains("Invalid training type"));
        assertTrue(exception.getMessage().contains("INVALID"));
    }

    @Test
    void testParse_withNull_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> TrainingTypeValidator.parse(null)
        );
        assertTrue(exception.getMessage().contains("Invalid training type"));
    }

    @Test
    void testParse_withEmptyString_shouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> TrainingTypeValidator.parse("")
        );
    }

    @Test
    void testGetAllValidTypes_shouldReturnAllTypes() {
        String allTypes = TrainingTypeValidator.getAllValidTypes();

        assertNotNull(allTypes);
        assertTrue(allTypes.contains("FITNESS"));
        assertTrue(allTypes.contains("YOGA"));
        assertTrue(allTypes.contains("CARDIO"));
        assertTrue(allTypes.contains("BOXING"));
        assertTrue(allTypes.contains("PILATES"));
        assertTrue(allTypes.contains("CROSSFIT"));
        assertTrue(allTypes.contains("SWIMMING"));
    }

    @Test
    void testGetAllValidTypes_shouldBeCommaSeparated() {
        String allTypes = TrainingTypeValidator.getAllValidTypes();

        assertTrue(allTypes.contains(", "));
        String[] types = allTypes.split(", ");
        assertEquals(7, types.length);
    }
}
