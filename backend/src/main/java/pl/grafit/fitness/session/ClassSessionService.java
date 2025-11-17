package pl.grafit.fitness.session;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.grafit.fitness.booking.Booking;
import pl.grafit.fitness.booking.BookingRepository;
import pl.grafit.fitness.shared.BusinessException;
import pl.grafit.fitness.shared.ResourceNotFoundException;

@Service
@Transactional
public class ClassSessionService {

    private final ClassSessionRepository sessionRepository;
    private final BookingRepository bookingRepository;
    private final Clock clock;

    public ClassSessionService(
            ClassSessionRepository sessionRepository,
            BookingRepository bookingRepository,
            Clock clock) {
        this.sessionRepository = sessionRepository;
        this.bookingRepository = bookingRepository;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public List<ClassSession> findAllSessions() {
        return sessionRepository.findAllByOrderByStartTimeAsc();
    }

    @Transactional(readOnly = true)
    public ClassSession findSession(Long id) {
        return sessionRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zajęcia o id %d nie istnieją".formatted(id)));
    }

    public ClassSession createSession(SessionCommand command) {
        validateStartTime(command.startTime());
        ensureUniqueTitleAndStart(command.title(), command.startTime(), null);

        ClassSession session = new ClassSession();
        applyCommand(session, command);
        return sessionRepository.save(session);
    }

    public ClassSession updateSession(Long id, SessionCommand command) {
        ClassSession session = findSession(id);
        validateStartTime(command.startTime());
        ensureUniqueTitleAndStart(command.title(), command.startTime(), id);

        applyCommand(session, command);
        return sessionRepository.save(session);
    }

    public void deleteSession(Long id) {
        ClassSession session = findSession(id);
        sessionRepository.delete(session);
    }

    public Booking addBooking(Long sessionId, String attendeeName) {
        ClassSession session = findSession(sessionId);
        validateStartTimeForBooking(session);
        ensureCapacity(session);

        Booking booking = new Booking();
        booking.setAttendeeName(attendeeName);
        session.addBooking(booking);

        return bookingRepository.save(booking);
    }

    public void cancelBooking(Long sessionId, Long bookingId) {
        ClassSession session = findSession(sessionId);
        enforceCancellationWindow(session);

        Booking booking =
                bookingRepository
                        .findById(bookingId)
                        .orElseThrow(() -> new ResourceNotFoundException("Rezerwacja nie istnieje"));

        if (!Objects.equals(session.getId(), booking.getSession().getId())) {
            throw new BusinessException("Ta rezerwacja należy do innych zajęć.");
        }

        session.removeBooking(booking);
        bookingRepository.delete(booking);
    }

    private void applyCommand(ClassSession session, SessionCommand command) {
        session.setTitle(command.title());
        session.setDescription(command.description());
        session.setStartTime(command.startTime());
        session.setCapacity(command.capacity());
    }

    private void validateStartTime(LocalDateTime startTime) {
        if (startTime == null || !startTime.isAfter(now())) {
            throw new BusinessException("Data zajęć musi być w przyszłości.");
        }
    }

    private void ensureUniqueTitleAndStart(String title, LocalDateTime startTime, Long currentId) {
        sessionRepository
                .findByTitleIgnoreCaseAndStartTime(title, startTime)
                .filter(existing -> currentId == null || !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new BusinessException("Zajęcia o tej nazwie i dacie już istnieją.");
                });
    }

    private void validateStartTimeForBooking(ClassSession session) {
        if (!session.getStartTime().isAfter(now())) {
            throw new BusinessException("Nie można zapisać się na zajęcia, które już się odbyły.");
        }
    }

    private void ensureCapacity(ClassSession session) {
        Integer capacity = session.getCapacity();
        if (capacity != null && session.getBookings().size() >= capacity) {
            throw new BusinessException("Brak wolnych miejsc na te zajęcia.");
        }
    }

    private void enforceCancellationWindow(ClassSession session) {
        LocalDateTime limit = session.getStartTime().minusHours(24);
        if (!now().isBefore(limit)) {
            throw new BusinessException("Nie można zrezygnować na mniej niż 24h przed zajęciami.");
        }
    }

    private LocalDateTime now() {
        return LocalDateTime.now(clock);
    }

    public record SessionCommand(
            String title, String description, LocalDateTime startTime, Integer capacity) {}
}

