package pl.grafit.fitness.config;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.grafit.fitness.session.ClassSession;
import pl.grafit.fitness.session.ClassSessionRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedSessions(ClassSessionRepository repository, Clock clock) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }

            LocalDateTime base = LocalDateTime.now(clock).withMinute(0).withSecond(0).withNano(0);

            ClassSession yoga = new ClassSession();
            yoga.setTitle("Poranna joga");
            yoga.setDescription("Rozciąganie i oddech na dobry start dnia.");
            yoga.setStartTime(base.plusDays(1).withHour(9));
            yoga.setCapacity(15);

            ClassSession hiit = new ClassSession();
            hiit.setTitle("HIIT Express");
            hiit.setDescription("Intensywny trening interwałowy.");
            hiit.setStartTime(base.plusDays(2).withHour(18));
            hiit.setCapacity(12);

            ClassSession pilates = new ClassSession();
            pilates.setTitle("Pilates Core");
            pilates.setDescription("Stabilizacja i wzmocnienie mięśni głębokich.");
            pilates.setStartTime(base.plusDays(3).withHour(17));
            pilates.setCapacity(null);

            repository.saveAll(List.of(yoga, hiit, pilates));
        };
    }
}

