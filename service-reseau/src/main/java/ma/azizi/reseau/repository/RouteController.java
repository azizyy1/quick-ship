package ma.azizi.reseau.controller;

import jakarta.validation.Valid;
import ma.azizi.reseau.dto.RouteRequest;
import ma.azizi.reseau.dto.RouteResponse;
import ma.azizi.reseau.service.RouteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping
    public List<RouteResponse> findAll() {
        return routeService.findAll();
    }

    @GetMapping("/{id}")
    public RouteResponse findById(@PathVariable Long id) {
        return routeService.findById(id);
    }

    @PostMapping
    public ResponseEntity<RouteResponse> create(
            @Valid @RequestBody RouteRequest request) {

        RouteResponse response = routeService.create(request);

        return ResponseEntity
                .created(URI.create("/api/routes/" + response.id()))
                .body(response);
    }

    @PutMapping("/{id}")
    public RouteResponse update(
            @PathVariable Long id,
            @Valid @RequestBody RouteRequest request) {

        return routeService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        routeService.delete(id);

        return ResponseEntity.noContent().build();
    }
}