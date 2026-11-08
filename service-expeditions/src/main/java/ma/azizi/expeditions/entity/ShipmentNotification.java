package ma.azizi.expeditions.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ShipmentNotification {

    @Id
    private Long shipmentId;

    private String message;

    protected ShipmentNotification() {
    }

    public ShipmentNotification(Long shipmentId) {
        this.shipmentId = shipmentId;
        this.message = "Expédition " + shipmentId + " créée";
    }

    public Long getShipmentId() {
        return shipmentId;
    }

    public String getMessage() {
        return message;
    }
}