package ma.azizi.expeditions.controller;

import java.util.List;

import jakarta.validation.Valid;
import ma.azizi.expeditions.service.ShipmentService.ShipmentDetails;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ma.azizi.expeditions.entity.Shipment;
import ma.azizi.expeditions.service.ShipmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @PostMapping
    public ResponseEntity<Shipment> create(@Valid @RequestBody ShipmentRequest request) {
        Shipment shipment = shipmentService.create(request.customerId(), request.routeId());
        return ResponseEntity.status(HttpStatus.CREATED).body(shipment);
    }

    @GetMapping
    public List<Shipment> findAll() {
        return shipmentService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Shipment> findById(@PathVariable("id") Long id) {
        return shipmentService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/details")
    public ShipmentDetails findDetails(@PathVariable("id") Long id) {
        return shipmentService.findDetails(id);
    }

    public record ShipmentRequest(
            @NotNull @Positive Long customerId,
            @NotNull @Positive Long routeId
    ) {
    }
}