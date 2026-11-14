package ma.azizi.expeditions.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ma.azizi.expeditions.entity.Shipment;
import ma.azizi.expeditions.service.ShipmentService;
import ma.azizi.expeditions.service.ShipmentService.ShipmentDetails;
import ma.azizi.expeditions.service.ShipmentCreationService;
import ma.azizi.expeditions.service.ShipmentCreationService.CreatedShipment;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;
    private final ShipmentCreationService shipmentCreationService;

    public ShipmentController(
            ShipmentService shipmentService,
            ShipmentCreationService shipmentCreationService
    ) {
        this.shipmentService = shipmentService;
        this.shipmentCreationService = shipmentCreationService;
    }

    @PostMapping
    public ResponseEntity<CreatedShipment> create(
            @RequestHeader(value = "Idempotency-Key", required = false) String key,
            @Valid @RequestBody ShipmentRequest request
    ) {
        CreatedShipment shipment =
                shipmentCreationService.create(key, request.customerId(), request.routeId());
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

    @PutMapping("/{id}")
    public Shipment update(
            @PathVariable("id") Long id,
            @Valid @RequestBody ShipmentRequest request
    ) {
        return shipmentService.update(id, request.customerId(), request.routeId());
    }

    @PostMapping("/{id}/transit")
    public Shipment startTransit(@PathVariable("id") Long id) {
        return shipmentService.startTransit(id);
    }

    @PostMapping("/{id}/deliver")
    public Shipment deliver(@PathVariable("id") Long id) {
        return shipmentService.deliver(id);
    }

    @PostMapping("/{id}/return")
    public Shipment returnShipment(@PathVariable("id") Long id) {
        return shipmentService.returnShipment(id);
    }

    public record ShipmentRequest(
            @NotNull @Positive Long customerId,
            @NotNull @Positive Long routeId
    ) {
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        shipmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}