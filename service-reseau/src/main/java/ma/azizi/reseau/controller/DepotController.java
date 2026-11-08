package ma.azizi.reseau.controller;

import jakarta.validation.Valid;
import ma.azizi.reseau.dto.DepotRequest;
import ma.azizi.reseau.dto.DepotResponse;
import ma.azizi.reseau.service.DepotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/depots")
public class DepotController {

    private final DepotService depotService;

    public DepotController(DepotService depotService) {
        this.depotService = depotService;
    }

    @GetMapping
    public List<DepotResponse> findAll() {
        return depotService.findAll();
    }

    @GetMapping("/{id}")
    public DepotResponse findById(@PathVariable Long id) {
        return depotService.findById(id);
    }

    @PostMapping
    public ResponseEntity<DepotResponse> create(
            @Valid @RequestBody DepotRequest request) {

        DepotResponse response = depotService.create(request);

        return ResponseEntity
                .created(URI.create("/api/depots/" + response.id()))
                .body(response);
    }

    @PutMapping("/{id}")
    public DepotResponse update(
            @PathVariable Long id,
            @Valid @RequestBody DepotRequest request) {

        return depotService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        depotService.delete(id);

        return ResponseEntity.noContent().build();
    }
}