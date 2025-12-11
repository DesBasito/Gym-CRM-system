package epam.gym.infrastructure.repositories;

import epam.gym.infrastructure.entities.Trainee;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long> {

    Optional<Trainee> findByUser_Username(String username);

    @Query("SELECT COUNT(t) > 0 FROM Trainee t WHERE t.user.username = :username AND t.user.password = :password")
    boolean authenticate(@Param("username") String username, @Param("password") String password);

    default List<Trainee> findAll(int offset, int limit) {
        return findAll(PageRequest.of(offset / limit, limit)).getContent();
    }
}