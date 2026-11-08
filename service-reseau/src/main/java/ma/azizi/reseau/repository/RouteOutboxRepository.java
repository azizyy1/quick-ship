package ma.azizi.reseau.repository;

import java.util.List;
import java.util.UUID;

import ma.azizi.reseau.entity.RouteOutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteOutboxRepository extends JpaRepository<RouteOutboxEvent, UUID> {

    List<RouteOutboxEvent> findTop20ByPublishedAtIsNullOrderByRouteIdAscRouteVersionAsc();
}