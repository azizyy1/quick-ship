package ma.azizi.expeditions.service;

import jakarta.persistence.EntityManager;
import ma.azizi.expeditions.entity.Shipment;
import ma.azizi.expeditions.entity.ShipmentCreationKey;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ShipmentCreationService {

    private final ShipmentService shipments;
    private final EntityManager entityManager;
    private final TransactionTemplate transaction;

    public ShipmentCreationService(
            ShipmentService shipments,
            EntityManager entityManager,
            PlatformTransactionManager transactionManager
    ) {
        this.shipments = shipments;
        this.entityManager = entityManager;
        this.transaction = new TransactionTemplate(transactionManager);
    }

    public CreatedShipment create(String key, Long customerId, Long routeId) {
        if (key == null) {
            return CreatedShipment.from(shipments.create(customerId, routeId));
        }

        if (key.isBlank() || key.length() > 128) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Clé d’idempotence invalide");
        }

        try {
            return transaction.execute(tx -> {
                ShipmentCreationKey existing =
                        entityManager.find(ShipmentCreationKey.class, key);
                if (existing != null) {
                    return replay(existing, customerId, routeId);
                }

                ShipmentCreationKey reservation =
                        new ShipmentCreationKey(key, customerId, routeId);
                try {
                    entityManager.persist(reservation);
                    entityManager.flush();
                } catch (jakarta.persistence.PersistenceException exception) {
                    throw new KeyReservationException(exception);
                }

                Shipment shipment = shipments.create(customerId, routeId);
                reservation.complete(shipment);
                return CreatedShipment.from(shipment);
            });
        } catch (KeyReservationException exception) {
            return transaction.execute(tx -> {
                ShipmentCreationKey existing =
                        entityManager.find(ShipmentCreationKey.class, key);
                if (existing == null) {
                    throw exception;
                }
                return replay(existing, customerId, routeId);
            });
        }
    }

    private CreatedShipment replay(
            ShipmentCreationKey existing, Long customerId, Long routeId
    ) {
        if (!existing.matches(customerId, routeId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cette clé d’idempotence est déjà utilisée avec d’autres données");
        }
        return new CreatedShipment(existing.getShipmentId(), customerId,
                routeId, existing.getStatus());
    }

    public record CreatedShipment(
            Long id, Long customerId, Long routeId, Shipment.Status status
    ) {
        static CreatedShipment from(Shipment shipment) {
            return new CreatedShipment(shipment.getId(), shipment.getCustomerId(),
                    shipment.getRouteId(), shipment.getStatus());
        }
    }

    private static class KeyReservationException extends RuntimeException {
        KeyReservationException(Throwable cause) {
            super(cause);
        }
    }
}