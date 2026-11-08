package ma.azizi.reseau.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record DepotRequest(
        @NotBlank(message = "Le nom est obligatoire")
        String nom,

        @NotBlank(message = "La ville est obligatoire")
        String ville,

        @NotNull(message = "La capacité est obligatoire")
        @PositiveOrZero(message = "La capacité doit être positive ou nulle")
        Integer capacite
) {
}