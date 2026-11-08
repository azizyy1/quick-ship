package ma.azizi.expeditions.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "route_projection")
public class RouteProjection {

    @Id
    @Column(name = "route_id")
    private Long routeId;

    @Column(name = "route_version", nullable = false)
    private long routeVersion;

    @Column(nullable = false)
    private boolean deleted;

    @Lob
    private String payload;

    protected RouteProjection() {
    }

    public RouteProjection(Long routeId, long routeVersion,
                           boolean deleted, String payload) {
        this.routeId = routeId;
        this.routeVersion = routeVersion;
        this.deleted = deleted;
        this.payload = payload;
    }

    public Long getRouteId() {
        return routeId;
    }

    public long getRouteVersion() {
        return routeVersion;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public String getPayload() {
        return payload;
    }
}