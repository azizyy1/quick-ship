package ma.azizi.expeditions.service;

import java.util.List;
import java.util.Optional;

import feign.FeignException;
import ma.azizi.expeditions.client.RouteClient;
import ma.azizi.expeditions.entity.Shipment;
import ma.azizi.expeditions.repository.ShipmentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final RouteClient routeClient;

    public ShipmentService(ShipmentRepository shipmentRepository, RouteClient routeClient) {
        this.shipmentRepository = shipmentRepository;
        this.routeClient = routeClient;
    }

    public Shipment create(Long customerId, Long routeId) {
        try {
            routeClient.getRoute(routeId);
        } catch (FeignException.NotFound exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Tournée introuvable", exception);
        } catch (FeignException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY, "Échec de la vérification de la tournée", exception);
        }

        return shipmentRepository.save(new Shipment(customerId, routeId));
    }

    public List<Shipment> findAll() {
        return shipmentRepository.findAll();
    }

    public Optional<Shipment> findById(Long id) {
        return shipmentRepository.findById(id);
    }
}