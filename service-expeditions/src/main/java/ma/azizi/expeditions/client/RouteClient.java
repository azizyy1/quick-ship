package ma.azizi.expeditions.client;

import java.time.LocalDateTime;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "service-reseau", url = "${services.reseau.url}")
public interface RouteClient {

    @GetMapping("/api/routes/{id}")
    RouteDetails getRoute(@PathVariable("id") Long id);

    record RouteDetails(
            Long id,
            String nom,
            String villeDepart,
            String villeArrivee,
            LocalDateTime dateDepart,
            Integer dureeEstimee,
            Integer capaciteColis
    ) {
    }
}