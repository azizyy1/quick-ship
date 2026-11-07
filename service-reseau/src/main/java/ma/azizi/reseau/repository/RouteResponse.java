package ma.azizi.reseau.dto;

import java.time.LocalDateTime;

public record RouteResponse(
        Long id,
        String nom,
        String villeDepart,
        String villeArrivee,
        LocalDateTime dateDepart,
        Integer dureeEstimee,
        Integer capaciteColis
) {
}