package epam.gym.util;

import epam.gym.constants.TrainingType;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TrainingTypeValidator {


    public boolean isValid(String trainingType) {
        if (trainingType == null) {
            return false;
        }
        return TrainingType.isExists(trainingType);
    }

    public TrainingType parse(String trainingType) {
        if (!isValid(trainingType)) {
            throw new IllegalArgumentException("Invalid training type: " + trainingType);
        }
        return TrainingType.valueOf(trainingType.toUpperCase());
    }

    public String getAllValidTypes() {
        return String.join(", ",
                java.util.Arrays.stream(TrainingType.values())
                        .map(Enum::name)
                        .toArray(String[]::new));
    }
}