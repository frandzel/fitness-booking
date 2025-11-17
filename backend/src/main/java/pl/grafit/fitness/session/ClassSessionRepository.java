package pl.grafit.fitness.session;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassSessionRepository extends JpaRepository<ClassSession, Long> {

    @EntityGraph(attributePaths = "bookings")
    List<ClassSession> findAllByOrderByStartTimeAsc();

    @Override
    @EntityGraph(attributePaths = "bookings")
    Optional<ClassSession> findById(Long id);

    Optional<ClassSession> findByTitleIgnoreCaseAndStartTime(String title, LocalDateTime startTime);
}

