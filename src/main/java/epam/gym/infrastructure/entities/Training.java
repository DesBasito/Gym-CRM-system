package epam.gym.infrastructure.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.HashSet;

@Getter
@Setter
@Entity
@Table(name = "trainings")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trainee_id", nullable = false)
    Trainee trainee;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trainer_id", nullable = false)
    Trainer trainer;

    @Size(max = 255)
    @NotNull
    @Column(name = "training_name", nullable = false)
    String trainingName;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "training_type_id", nullable = false)
    TrainingType trainingType;

    @NotNull
    @Column(name = "training_date", nullable = false)
    LocalDate trainingDate;

    @NotNull
    @Column(name = "training_duration", nullable = false)
    Integer trainingDuration;

    @PreRemove
    private void removeAssociations() {
        if (trainee != null && trainee.getTrainings() != null) {
            trainee.getTrainings().remove(this);
        }
        if (trainer != null && trainer.getTrainings() != null) {
            trainer.getTrainings().remove(this);
        }
    }

}