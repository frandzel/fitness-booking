package pl.grafit.fitness.web.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record SessionRequest(
        @NotBlank(message = "Nazwa jest wymagana") String title,
        @Size(max = 500, message = "Opis może mieć maksymalnie 500 znaków") String description,
        @NotNull @Future(message = "Data musi być w przyszłości") LocalDateTime startTime,
        @Positive(message = "Limit miejsc musi być dodatni")
                Integer capacity) {}

