package pl.grafit.fitness.session;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.grafit.fitness.booking.Booking;
import pl.grafit.fitness.booking.BookingRepository;
import pl.grafit.fitness.shared.BusinessException;

@ExtendWith(MockitoExtension.class)
class ClassSessionServiceTest {

    @Mock
    private ClassSessionRepository sessionRepository;

    @Mock
    private BookingRepository bookingRepository;

    private Clock clock;
    private ClassSessionService service;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.parse("2025-01-01T08:00:00Z"), ZoneId.of("UTC"));
        service = new ClassSessionService(sessionRepository, bookingRepository, clock);
    }

    @Test
    void cancelBookingShouldFailWhenLessThan24HoursLeft() {
        ClassSession session = sessionWithStart(now().plusHours(10));
        Booking booking = bookingForSession(5L, session);

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        assertThatThrownBy(() -> service.cancelBooking(1L, 5L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("24h");

        verify(bookingRepository, never()).delete(booking);
    }

    @Test
    void cancelBookingShouldDeleteWhenAllowed() {
        ClassSession session = sessionWithStart(now().plusDays(2));
        Booking booking = bookingForSession(7L, session);

        when(sessionRepository.findById(2L)).thenReturn(Optional.of(session));
        when(bookingRepository.findById(7L)).thenReturn(Optional.of(booking));

        service.cancelBooking(2L, 7L);

        verify(bookingRepository).delete(booking);
    }

    private LocalDateTime now() {
        return LocalDateTime.ofInstant(clock.instant(), clock.getZone());
    }

    private ClassSession sessionWithStart(LocalDateTime startTime) {
        ClassSession session = new ClassSession();
        session.setId(1L);
        session.setTitle("Test");
        session.setStartTime(startTime);
        return session;
    }

    private Booking bookingForSession(Long id, ClassSession session) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setAttendeeName("Jan");
        booking.setSession(session);
        session.addBooking(booking);
        return booking;
    }
}

