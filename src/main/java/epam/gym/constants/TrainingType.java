package epam.gym.constants;

import java.util.Arrays;

public enum TrainingType {
    FITNESS,
    YOGA,
    CARDIO,
    BOXING,
    PILATES,
    CROSSFIT,
    SWIMMING;

    public static boolean isExists(String t){
        try {
            TrainingType.valueOf(t.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
