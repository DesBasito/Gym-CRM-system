package epam.gym.infrastructure.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = TypeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsTrainingTypeValid {
    String message() default "Training type not valid!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
