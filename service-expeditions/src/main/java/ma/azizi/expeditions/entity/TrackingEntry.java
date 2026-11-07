package ma.azizi.expeditions.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tracking_entries")
public class TrackingEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    @Column(nullable = false, length = 255)
    private String message;

    @Column(nullable = false)
    private Instant recordedAt;

    protected TrackingEntry() {
    }

    public TrackingEntry(Shipment shipment, String message) {
        this.shipment = shipment;
        this.message = message;
        this.recordedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public String getMessage() {
        return message;
    }

    public Instant getRecordedAt() {
        return recordedAt;
    }
}