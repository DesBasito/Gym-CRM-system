package epam.gym.infrastructure.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Long id;

    @NotNull
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
    List<Trainer> trainers = new ArrayList<>();

    @PreRemove
    private void removeAssociations() {
        for (Trainer trainer : new ArrayList<>(trainers)) {
            trainer.getTrainees().remove(this);
        }
    }

    @OneToMany(mappedBy = "trainee")
    Set<Training> trainings = new LinkedHashSet<>();

}