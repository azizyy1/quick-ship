package ma.azizi.reseau.dto;

public record DepotResponse(
        Long id,
        String nom,
        String ville,
        Integer capacite
) {
}