package ma.azizi.expeditions.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import feign.FeignException;
import ma.azizi.expeditions.client.RouteClient;
import ma.azizi.expeditions.client.RouteClient.RouteDetails;
import ma.azizi.expeditions.entity.Shipment;
import ma.azizi.expeditions.repository.ShipmentRepository;
import ma.azizi.expeditions.repository.TrackingEntryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final RouteClient routeClient;
    private final TrackingEntryRepository trackingEntryRepository;

    public ShipmentService(
            ShipmentRepository shipmentRepository,
            RouteClient routeClient,
            TrackingEntryRepository trackingEntryRepository
    ) {
        this.shipmentRepository = shipmentRepository;
        this.routeClient = routeClient;
        this.trackingEntryRepository = trackingEntryRepository;
    }

    public Shipment create(Long customerId, Long routeId) {
        verifyRoute(routeId);
        return shipmentRepository.save(new Shipment(customerId, routeId));
    }

    public Shipment update(Long id, Long customerId, Long routeId) {
        Shipment shipment = findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Expédition introuvable"));

        verifyRoute(routeId);
        shipment.updateReferences(customerId, routeId);
        return shipmentRepository.save(shipment);
    }

    private void verifyRoute(Long routeId) {
        try {
            routeClient.getRoute(routeId);
        } catch (FeignException.NotFound exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Tournée introuvable", exception);
        } catch (FeignException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY, "Échec de la vérification de la tournée", exception);
        }
    }

    public List<Shipment> findAll() {
        return shipmentRepository.findAll();
    }

    public Optional<Shipment> findById(Long id) {
        return shipmentRepository.findById(id);
    }

    public ShipmentDetails findDetails(Long id) {
        Shipment shipment = findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Expédition introuvable"));

        RouteDetails route;
        try {
            route = routeClient.getRoute(shipment.getRouteId());
        } catch (FeignException.NotFound exception) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Tournée associée introuvable", exception);
        } catch (FeignException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY, "Impossible de consulter la tournée", exception);
        }

        List<TrackingDetails> tracking = trackingEntryRepository
                .findByShipment_IdOrderByRecordedAtAscIdAsc(id)
                .stream()
                .map(entry -> new TrackingDetails(
                        entry.getId(), entry.getMessage(), entry.getRecordedAt()))
                .toList();

        return new ShipmentDetails(
                shipment.getId(), shipment.getCustomerId(), route, tracking);
    }

    public record ShipmentDetails(
            Long id,
            Long customerId,
            RouteDetails route,
            List<TrackingDetails> tracking
    ) {
    }

    public record TrackingDetails(Long id, String message, Instant recordedAt) {
    }
}