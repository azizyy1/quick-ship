package ma.azizi.expeditions.service;

import java.util.List;

import ma.azizi.expeditions.entity.Shipment;
import ma.azizi.expeditions.entity.TrackingEntry;
import ma.azizi.expeditions.repository.ShipmentRepository;
import ma.azizi.expeditions.repository.TrackingEntryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TrackingEntryService {

    private final ShipmentRepository shipmentRepository;
    private final TrackingEntryRepository trackingEntryRepository;

    public TrackingEntryService(
            ShipmentRepository shipmentRepository,
            TrackingEntryRepository trackingEntryRepository
    ) {
        this.shipmentRepository = shipmentRepository;
        this.trackingEntryRepository = trackingEntryRepository;
    }

    @Transactional
    public TrackingEntry addEntry(Long shipmentId, String message) {
        Shipment shipment = requireShipment(shipmentId);
        return trackingEntryRepository.save(new TrackingEntry(shipment, message));
    }

    @Transactional(readOnly = true)
    public List<TrackingEntry> findHistory(Long shipmentId) {
        requireShipment(shipmentId);
        return trackingEntryRepository.findByShipment_IdOrderByRecordedAtAscIdAsc(shipmentId);
    }

    private Shipment requireShipment(Long shipmentId) {
        return shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Expédition introuvable"));
    }
}