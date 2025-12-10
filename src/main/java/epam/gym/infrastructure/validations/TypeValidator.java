package epam.gym.infrastructure.validations;

import epam.gym.constants.TrainingType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TypeValidator implements ConstraintValidator<IsTrainingTypeValid, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        context.disableDefaultConstraintViolation();

        if(!TrainingType.isExists(value)){
            context.buildConstraintViolationWithTemplate("Тип тренировки: "+value+" не найдена в системе!")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
