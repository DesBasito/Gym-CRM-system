package epam.gym.infrastructure.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "training_types")
public class TrainingType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "training_type_name", nullable = false)
    private epam.gym.constants.TrainingType trainingTypeName;

}