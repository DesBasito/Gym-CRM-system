package epam.gym.infrastructure.repositories;

import epam.gym.infrastructure.entities.Trainer;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    Optional<Trainer> findByUser_Username(String username);

    @Query("SELECT COUNT(t) > 0 FROM Trainer t WHERE t.user.username = :username AND t.user.password = :password")
    boolean authenticate(@Param("username") String username, @Param("password") String password);

    @Query("""
            SELECT DISTINCT tr FROM Trainer tr
            LEFT JOIN tr.trainees t
            LEFT JOIN t.user tu
            WHERE tr.user.isActive = true
            AND (tu.username IS NULL OR tu.username != :traineeUsername)
            """)
    List<Trainer> findAllNotAssignedToTrainee(@Param("traineeUsername") String traineeUsername);

    default List<Trainer> findAll(int offset, int limit) {
        return findAll(PageRequest.of(offset / limit, limit)).getContent();
    }
}