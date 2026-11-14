package ma.azizi.expeditions.messaging;

import ma.azizi.expeditions.entity.Shipment.Status;

public record ShipmentStatusChanged(
        Long shipmentId,
        Long customerId,
        Status status
) {
}