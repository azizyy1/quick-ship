package ma.azizi.expeditions.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import ma.azizi.expeditions.entity.Shipment.Status;

@Entity
public class ShipmentStatusNotification {

    @Id
    private String id;

    private Long shipmentId;
    private Long customerId;
    private String status;
    private String message;

    protected ShipmentStatusNotification() {
    }

    public ShipmentStatusNotification(Long shipmentId, Long customerId, Status status) {
        this.id = shipmentId + ":" + status.name();
        this.shipmentId = shipmentId;
        this.customerId = customerId;
        this.status = status.name();
        this.message = switch (status) {
            case LIVRE -> "Votre expédition " + shipmentId + " a été livrée.";
            case RETOUR -> "Votre expédition " + shipmentId + " a été retournée.";
            default -> throw new IllegalArgumentException("Statut non notifiable : " + status);
        };
    }

    public String getId() { return id; }
    public Long getShipmentId() { return shipmentId; }
    public Long getCustomerId() { return customerId; }
    public String getStatus() { return status; }
    public String getMessage() { return message; }
}