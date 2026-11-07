package ma.azizi.expeditions.service;

import java.util.List;
import java.util.Optional;

import ma.azizi.expeditions.entity.Shipment;
import ma.azizi.expeditions.repository.ShipmentRepository;
import org.springframework.stereotype.Service;

@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;

    public ShipmentService(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    public Shipment create(Long customerId, Long routeId) {
        return shipmentRepository.save(new Shipment(customerId, routeId));
    }

    public List<Shipment> findAll() {
        return shipmentRepository.findAll();
    }

    public Optional<Shipment> findById(Long id) {
        return shipmentRepository.findById(id);
    }
}