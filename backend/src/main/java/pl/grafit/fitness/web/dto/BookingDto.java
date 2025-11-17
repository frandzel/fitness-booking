package pl.grafit.fitness.web.dto;

import java.time.LocalDateTime;

public record BookingDto(Long id, String attendeeName, LocalDateTime createdAt) {}

