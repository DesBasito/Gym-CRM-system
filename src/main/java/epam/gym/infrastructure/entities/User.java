package epam.gym.infrastructure.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "first_name", nullable = false)
    String firstName;

    @Size(max = 255)
    @NotNull
    @Column(name = "last_name", nullable = false)
    String lastName;

    @Size(max = 255)
    @NotNull
    @Column(name = "username", nullable = false)
    String username;

    @Size(max = 255)
    @NotNull
    @Column(name = "password", nullable = false)
    String password;

    @NotNull
    @ColumnDefault("true")
    @Column(name = "is_active", nullable = false)
    Boolean isActive = true;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    Trainee trainee;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    Trainer trainer;

}