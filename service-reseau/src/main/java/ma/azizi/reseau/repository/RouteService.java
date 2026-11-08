package ma.azizi.reseau.service;

import java.util.List;
import java.util.UUID;

import ma.azizi.reseau.dto.RouteRequest;
import ma.azizi.reseau.dto.RouteResponse;
import ma.azizi.reseau.entity.Route;
import ma.azizi.reseau.entity.RouteOutboxEvent;
import ma.azizi.reseau.repository.RouteOutboxRepository;
import ma.azizi.reseau.repository.RouteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.json.JsonMapper;

@Service
@Transactional(readOnly = true)
public class RouteService {

    private final RouteRepository routeRepository;
    private final RouteOutboxRepository outboxRepository;
    private final JsonMapper jsonMapper;

    public RouteService(RouteRepository routeRepository,
                        RouteOutboxRepository outboxRepository,
                        JsonMapper jsonMapper) {
        this.routeRepository = routeRepository;
        this.outboxRepository = outboxRepository;
        this.jsonMapper = jsonMapper;
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

        Route saved = routeRepository.saveAndFlush(route);
        recordEvent("RouteCreated", saved, saved.getVersion());
        return toResponse(saved);
    }

    @Transactional
    public RouteResponse update(Long id, RouteRequest request) {
        Route route = getRoute(id);
        long previousVersion = route.getVersion();

        route.modifier(
                request.nom().strip(),
                request.villeDepart().strip(),
                request.villeArrivee().strip(),
                request.dateDepart(),
                request.dureeEstimee(),
                request.capaciteColis()
        );

        routeRepository.flush();
        if (route.getVersion() != previousVersion) {
            recordEvent("RouteUpdated", route, route.getVersion());
        }
        return toResponse(route);
    }

    @Transactional
    public void delete(Long id) {
        Route route = getRoute(id);
        recordEvent("RouteDeleted", route, route.getVersion() + 1);
        routeRepository.delete(route);
    }

    private void recordEvent(String eventType, Route route, long version) {
        String payload = jsonMapper.writeValueAsString(toResponse(route));
        outboxRepository.save(new RouteOutboxEvent(
                UUID.randomUUID(), route.getId(), version, eventType, payload
        ));
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