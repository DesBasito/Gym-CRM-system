package epam.gym.constants;

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
