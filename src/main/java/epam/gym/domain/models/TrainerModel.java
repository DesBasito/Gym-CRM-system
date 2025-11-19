package epam.gym.domain.models;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrainerModel extends UserModel {
    Long id;
    Long userId;
    String specialization;
}
