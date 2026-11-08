package ma.azizi.expeditions.repository;

import ma.azizi.expeditions.entity.RouteProjection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteProjectionRepository
        extends JpaRepository<RouteProjection, Long> {
}