package pl.grafit.fitness.web;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.grafit.fitness.booking.Booking;
import pl.grafit.fitness.session.ClassSession;
import pl.grafit.fitness.session.ClassSessionService;
import pl.grafit.fitness.web.dto.BookingDto;
import pl.grafit.fitness.web.dto.BookingRequest;
import pl.grafit.fitness.web.dto.ClassSessionDto;
import pl.grafit.fitness.web.dto.SessionRequest;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final ClassSessionService service;
    private final SessionMapper mapper;

    public SessionController(ClassSessionService service, SessionMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<ClassSessionDto> listSessions() {
        return service.findAllSessions().stream().map(mapper::toDto).toList();
    }

    @PostMapping
    public ResponseEntity<ClassSessionDto> createSession(@Valid @RequestBody SessionRequest request) {
        ClassSession session = service.createSession(toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(session));
    }

    @PutMapping("/{id}")
    public ClassSessionDto updateSession(
            @PathVariable Long id, @Valid @RequestBody SessionRequest request) {
        ClassSession session = service.updateSession(id, toCommand(request));
        return mapper.toDto(session);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSession(@PathVariable Long id) {
        service.deleteSession(id);
    }

    @GetMapping("/{id}/bookings")
    public List<BookingDto> listBookings(@PathVariable Long id) {
        ClassSession session = service.findSession(id);
        return mapper.toDto(session).bookings();
    }

    @PostMapping("/{id}/bookings")
    public ResponseEntity<BookingDto> addBooking(
            @PathVariable Long id, @Valid @RequestBody BookingRequest request) {
        Booking booking = service.addBooking(id, request.attendeeName());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(booking));
    }

    @DeleteMapping("/{id}/bookings/{bookingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelBooking(@PathVariable Long id, @PathVariable Long bookingId) {
        service.cancelBooking(id, bookingId);
    }

    private ClassSessionService.SessionCommand toCommand(SessionRequest request) {
        return new ClassSessionService.SessionCommand(
                request.title(), request.description(), request.startTime(), request.capacity());
    }
}

