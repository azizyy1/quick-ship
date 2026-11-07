package ma.azizi.reseau.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record RouteRequest(
        @NotBlank String nom,
        @NotBlank String villeDepart,
        @NotBlank String villeArrivee,
        @NotNull LocalDateTime dateDepart,
        @NotNull @Positive Integer dureeEstimee,
        @NotNull @Positive Integer capaciteColis
) {
}