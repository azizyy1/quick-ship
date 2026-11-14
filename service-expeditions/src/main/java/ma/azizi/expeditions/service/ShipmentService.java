package ma.azizi.expeditions.service;

import feign.FeignException;
import ma.azizi.expeditions.client.CustomerClient;
import ma.azizi.expeditions.client.RouteClient;
import ma.azizi.expeditions.client.RouteClient.RouteDetails;
import ma.azizi.expeditions.entity.Shipment;
import ma.azizi.expeditions.entity.Shipment.Status;
import ma.azizi.expeditions.messaging.ShipmentCreated;
import ma.azizi.expeditions.messaging.ShipmentStatusChanged;
import ma.azizi.expeditions.repository.ShipmentRepository;
import ma.azizi.expeditions.repository.TrackingEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ma.azizi.expeditions.client.CustomerClient.CustomerDetails;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ShipmentService {

    private static final Logger log = LoggerFactory.getLogger(ShipmentService.class);

    private final ShipmentRepository shipmentRepository;
    private final RouteClient routeClient;
    private final CustomerClient customerClient;
    private final TrackingEntryRepository trackingEntryRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ShipmentService(
            ShipmentRepository shipmentRepository,
            RouteClient routeClient,
            CustomerClient customerClient,
            TrackingEntryRepository trackingEntryRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.shipmentRepository = shipmentRepository;
        this.routeClient = routeClient;
        this.customerClient = customerClient;
        this.trackingEntryRepository = trackingEntryRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Shipment create(Long customerId, Long routeId) {
        verifyRoute(routeId);
        verifyCustomer(customerId);

        Shipment shipment = shipmentRepository.save(new Shipment(customerId, routeId));
        eventPublisher.publishEvent(new ShipmentCreated(shipment.getId()));
        return shipment;
    }

    @Transactional
    public Shipment update(Long id, Long customerId, Long routeId) {
        verifyRoute(routeId);
        verifyCustomer(customerId);

        Shipment shipment = findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Expédition introuvable"));

        shipment.updateReferences(customerId, routeId);
        return shipment;
    }

    @Transactional
    public void delete(Long id) {
        Shipment shipment = findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Expédition introuvable"));

        try {
            shipmentRepository.delete(shipment);
            shipmentRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cette expédition est encore référencée, notamment par son suivi",
                    exception);
        }
    }

    private void verifyRoute(Long routeId) {
        if (routeId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "L’identifiant de la tournée est obligatoire");
        }

        try {
            routeClient.getRoute(routeId);
        } catch (FeignException.NotFound exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Tournée introuvable", exception);
        } catch (FeignException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY, "Impossible de vérifier la tournée", exception);
        }
    }

    private void verifyCustomer(Long customerId) {
        if (customerId == null || customerId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Identifiant client invalide");
        }

        try {
            customerClient.getCustomer(customerId);
        } catch (FeignException.NotFound exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Client introuvable", exception);
        } catch (FeignException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY, "Impossible de vérifier le client", exception);
        }
    }

    @Transactional
    public Shipment startTransit(Long id) {
        return transition(id, Set.of(Status.EN_ATTENTE), Status.EN_TRANSIT);
    }

    @Transactional
    public Shipment deliver(Long id) {
        Shipment shipment = transition(id, Set.of(Status.EN_TRANSIT), Status.LIVRE);
        publishStatusChanged(shipment);
        return shipment;
    }

    @Transactional
    public Shipment returnShipment(Long id) {
        Shipment shipment = transition(
                id, Set.of(Status.EN_TRANSIT, Status.LIVRE), Status.RETOUR);
        publishStatusChanged(shipment);
        return shipment;
    }

    private void publishStatusChanged(Shipment shipment) {
        eventPublisher.publishEvent(new ShipmentStatusChanged(
                shipment.getId(),
                shipment.getCustomerId(),
                shipment.getStatus()
        ));
    }

    private Shipment transition(Long id, Set<Status> allowedStatuses, Status target) {
        Shipment shipment = shipmentRepository.findByIdForStatusChange(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Expédition introuvable"));

        Status current = shipment.getStatus();

        log.info("Expédition {} : actuel={}, autorisés={}, cible={}",
                id, current, allowedStatuses, target);

        if (!allowedStatuses.contains(current)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Impossible de passer de " + current + " à " + target);
        }

        shipment.changeStatus(target);
        return shipment;
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

        CustomerDetails customer;
        try {
            customer = customerClient.getCustomer(shipment.getCustomerId());
        } catch (FeignException.NotFound exception) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Client associé introuvable", exception);
        } catch (FeignException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY, "Impossible de consulter le client", exception);
        }

        List<TrackingDetails> tracking = trackingEntryRepository
                .findByShipment_IdOrderByRecordedAtAscIdAsc(id)
                .stream()
                .map(entry -> new TrackingDetails(
                        entry.getId(), entry.getMessage(), entry.getRecordedAt()))
                .toList();

        return new ShipmentDetails(
                shipment.getId(),
                shipment.getCustomerId(),
                route,
                tracking,
                customer
        );
    }

    public record ShipmentDetails(
            Long id,
            Long customerId,
            RouteDetails route,
            List<TrackingDetails> tracking,
            CustomerDetails customer
    ) {
    }

    public record TrackingDetails(Long id, String message, Instant recordedAt) {
    }
}