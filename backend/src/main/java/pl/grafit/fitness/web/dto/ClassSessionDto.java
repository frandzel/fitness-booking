package pl.grafit.fitness.web.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ClassSessionDto(
        Long id,
        String title,
        String description,
        LocalDateTime startTime,
        Integer capacity,
        int attendeeCount,
        List<BookingDto> bookings) {}

