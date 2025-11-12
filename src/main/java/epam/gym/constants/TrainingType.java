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
        return Arrays.asList(TrainingType.values()).contains(TrainingType.valueOf(t.toUpperCase()));
    }
}
