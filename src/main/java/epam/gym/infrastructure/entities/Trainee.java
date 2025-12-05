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
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "trainees")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Trainee implements UserHolder {
    @Id
    @Column(name = "user_id", nullable = false)
    Long id;

    @NotNull
    @MapsId
    @OneToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(name = "date_of_birth")
    LocalDate dateOfBirth;

    @Size(max = 255)
    @Column(name = "address")
    private String address;


    @ManyToMany(mappedBy = "trainees")
    Set<Trainer> trainers = new HashSet<>();


    public void removeAssociations() {
        for (Trainer trainer : new HashSet<>(trainers)) {
            trainer.getTrainees().remove(this);
        }
        for (Training t : new LinkedHashSet<>(trainings)) {
            t.setTrainee(null);
        }
        trainings.clear();
    }

    @OneToMany(mappedBy = "trainee", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<Training> trainings = new LinkedHashSet<>();

    public void addTraining(Training training) {
        trainings.add(training);
    }
}