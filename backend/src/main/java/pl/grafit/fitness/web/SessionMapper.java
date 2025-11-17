package pl.grafit.fitness.web;

import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;
import pl.grafit.fitness.booking.Booking;
import pl.grafit.fitness.session.ClassSession;
import pl.grafit.fitness.web.dto.BookingDto;
import pl.grafit.fitness.web.dto.ClassSessionDto;

@Component
public class SessionMapper {

    public ClassSessionDto toDto(ClassSession session) {
        List<BookingDto> attendees = session.getBookings().stream()
                .sorted(Comparator.comparing(Booking::getCreatedAt))
                .map(this::toDto)
                .toList();

        return new ClassSessionDto(
                session.getId(),
                session.getTitle(),
                session.getDescription(),
                session.getStartTime(),
                session.getCapacity(),
                attendees.size(),
                attendees);
    }

    public BookingDto toDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getAttendeeName(),
                booking.getCreatedAt());
    }
}

