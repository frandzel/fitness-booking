package pl.grafit.fitness.web.dto;

import jakarta.validation.constraints.NotBlank;

public record BookingRequest(
        @NotBlank(message = "Imię uczestnika jest wymagane") String attendeeName) {}

