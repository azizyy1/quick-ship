package ma.azizi.expeditions.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "shipment_creation_keys")
public class ShipmentCreationKey {

    @Id
    @Column(name = "request_key", length = 128)
    private String key;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false)
    private Long routeId;

    private Long shipmentId;

    @Enumerated(EnumType.STRING)
    private Shipment.Status status;

    protected ShipmentCreationKey() {
    }

    public ShipmentCreationKey(String key, Long customerId, Long routeId) {
        this.key = key;
        this.customerId = customerId;
        this.routeId = routeId;
    }

    public boolean matches(Long customerId, Long routeId) {
        return this.customerId.equals(customerId) && this.routeId.equals(routeId);
    }

    public void complete(Shipment shipment) {
        this.shipmentId = shipment.getId();
        this.status = shipment.getStatus();
    }

    public Long getShipmentId() {
        return shipmentId;
    }

    public Shipment.Status getStatus() {
        return status;
    }
}