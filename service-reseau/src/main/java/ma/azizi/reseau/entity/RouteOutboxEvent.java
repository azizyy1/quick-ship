package ma.azizi.reseau.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "route_outbox")
public class RouteOutboxEvent {

    @Id
    private UUID eventId;

    @Column(nullable = false)
    private UUID correlationId;

    @Column(nullable = false)
    private Long routeId;

    @Column(nullable = false)
    private long routeVersion;

    @Column(nullable = false)
    private String eventType;

    @Lob
    @Column(nullable = false)
    private String payload;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant publishedAt;

    protected RouteOutboxEvent() {
    }

    public RouteOutboxEvent(UUID correlationId, Long routeId, long routeVersion,
                            String eventType, String payload) {
        this.eventId = UUID.randomUUID();
        this.correlationId = correlationId;
        this.routeId = routeId;
        this.routeVersion = routeVersion;
        this.eventType = eventType;
        this.payload = payload;
        this.createdAt = Instant.now();
    }
    public UUID getEventId() { return eventId; }
    public UUID getCorrelationId() { return correlationId; }
    public Long getRouteId() { return routeId; }
    public long getRouteVersion() { return routeVersion; }
    public String getEventType() { return eventType; }
    public String getPayload() { return payload; }

    public void markPublished() {
        this.publishedAt = Instant.now();
    }
}