package ma.azizi.expeditions.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
@Table(name = "shipments")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false)
    private Long routeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.EN_ATTENTE;

    public enum Status {
        EN_ATTENTE,
        EN_TRANSIT,
        LIVRE,
        RETOUR
    }

    public Status getStatus() {
        return status == null ? Status.EN_ATTENTE : status;
    }

    public void changeStatus(Status status) {
        this.status = status;
    }

    protected Shipment() {
    }

    public Shipment(Long customerId, Long routeId) {
        this.customerId = customerId;
        this.routeId = routeId;
    }

    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public Long getRouteId() {
        return routeId;
    }
    public void updateReferences(Long customerId, Long routeId) {
        this.customerId = customerId;
        this.routeId = routeId;
    }
}