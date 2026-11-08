package ma.azizi.expeditions.controller;

import java.time.Instant;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ma.azizi.expeditions.entity.TrackingEntry;
import ma.azizi.expeditions.service.TrackingEntryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shipments/{shipmentId}/tracking")
public class TrackingEntryController {

    private final TrackingEntryService trackingEntryService;

    public TrackingEntryController(TrackingEntryService trackingEntryService) {
        this.trackingEntryService = trackingEntryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrackingResponse addEntry(
            @PathVariable("shipmentId") Long shipmentId,
            @Valid @RequestBody TrackingRequest request
    ) {
        TrackingEntry entry = trackingEntryService.addEntry(shipmentId, request.message());
        return toResponse(shipmentId, entry);
    }

    @GetMapping
    public List<TrackingResponse> findHistory(@PathVariable("shipmentId") Long shipmentId) {
        return trackingEntryService.findHistory(shipmentId).stream()
                .map(entry -> toResponse(shipmentId, entry))
                .toList();
    }

    private TrackingResponse toResponse(Long shipmentId, TrackingEntry entry) {
        return new TrackingResponse(entry.getId(), shipmentId, entry.getMessage(), entry.getRecordedAt());
    }

    public record TrackingRequest(@NotBlank @Size(max = 255) String message) {
    }

    public record TrackingResponse(Long id, Long shipmentId, String message, Instant recordedAt) {
    }
}