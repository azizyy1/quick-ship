package ma.azizi.reseau.service;

import ma.azizi.reseau.dto.RouteRequest;
import ma.azizi.reseau.dto.RouteResponse;
import ma.azizi.reseau.entity.Route;
import ma.azizi.reseau.repository.RouteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RouteService {

    private final RouteRepository routeRepository;

    public RouteService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    public List<RouteResponse> findAll() {
        return routeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public RouteResponse findById(Long id) {
        return toResponse(getRoute(id));
    }

    @Transactional
    public RouteResponse create(RouteRequest request) {
        Route route = new Route(
                request.nom().strip(),
                request.villeDepart().strip(),
                request.villeArrivee().strip(),
                request.dateDepart(),
                request.dureeEstimee(),
                request.capaciteColis()
        );

        return toResponse(routeRepository.save(route));
    }

    @Transactional
    public RouteResponse update(Long id, RouteRequest request) {
        Route route = getRoute(id);

        route.modifier(
                request.nom().strip(),
                request.villeDepart().strip(),
                request.villeArrivee().strip(),
                request.dateDepart(),
                request.dureeEstimee(),
                request.capaciteColis()
        );

        return toResponse(route);
    }

    @Transactional
    public void delete(Long id) {
        routeRepository.delete(getRoute(id));
    }

    private Route getRoute(Long id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Tournée introuvable avec l'identifiant " + id
                ));
    }

    private RouteResponse toResponse(Route route) {
        return new RouteResponse(
                route.getId(),
                route.getNom(),
                route.getVilleDepart(),
                route.getVilleArrivee(),
                route.getDateDepart(),
                route.getDureeEstimee(),
                route.getCapaciteColis()
        );
    }
}