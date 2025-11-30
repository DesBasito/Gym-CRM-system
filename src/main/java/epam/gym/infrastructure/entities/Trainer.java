package epam.gym.infrastructure.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "trainers")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Trainer implements UserHolder {
    @Id
    @Column(name = "user_id", nullable = false)
    Long id;

    @NotNull
    @OneToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "specialization_id", nullable = false)
    TrainingType specialization;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "trainers_trainees",
            joinColumns = @JoinColumn(name = "trainer_id"),
            inverseJoinColumns = @JoinColumn(name = "trainee_id")
    )
    Set<Trainee> trainees = new HashSet<>();

    @PreRemove
    private void removeAssociations() {
        for (Trainee trainee : new HashSet<>(trainees)) {
            trainee.getTrainers().remove(this);
        }
    }

    @OneToMany(mappedBy = "trainer")
    Set<Training> trainings = new LinkedHashSet<>();

}