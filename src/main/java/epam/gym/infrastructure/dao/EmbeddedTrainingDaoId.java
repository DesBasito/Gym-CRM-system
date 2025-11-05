package epam.gym.infrastructure.dao;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmbeddedTrainingDaoId {
    String trainerId;
    String traineeId;
    String trainingName;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        epam.gym.infrastructure.dao.EmbeddedTrainingDaoId that = (epam.gym.infrastructure.dao.EmbeddedTrainingDaoId) o;
        return Objects.equals(trainerId, that.trainerId) && Objects.equals(traineeId, that.traineeId) && Objects.equals(trainingName, that.trainingName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trainerId, traineeId, trainingName);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EmbeddedTrainingId{");
        sb.append("trainerId='").append(trainerId).append('\'');
        sb.append(", traineeId='").append(traineeId).append('\'');
        sb.append(", trainingName='").append(trainingName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

