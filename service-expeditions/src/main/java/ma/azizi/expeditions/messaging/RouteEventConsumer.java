package ma.azizi.expeditions.messaging;

import ma.azizi.expeditions.entity.RouteProjection;
import ma.azizi.expeditions.repository.RouteProjectionRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Component
public class RouteEventConsumer {

    private final RouteProjectionRepository repository;
    private final JsonMapper jsonMapper;

    public RouteEventConsumer(RouteProjectionRepository repository, JsonMapper jsonMapper) {
        this.repository = repository;
        this.jsonMapper = jsonMapper;
    }

    @KafkaListener(topics = "quickship.routes.events")
    @Transactional
    public void consume(String message) {
        JsonNode event = jsonMapper.readTree(message);
        long routeId = event.required("routeId").longValue();
        long version = event.required("version").longValue();
        String eventType = event.required("eventType").asText();

        boolean deleted = switch (eventType) {
            case "RouteCreated", "RouteUpdated" -> false;
            case "RouteDeleted" -> true;
            default -> throw new IllegalArgumentException(
                    "Type d'événement tournée inconnu : " + eventType);
        };

        RouteProjection current = repository.findById(routeId).orElse(null);
        if (current != null && current.getRouteVersion() >= version) {
            return;
        }

        String payload = deleted
                ? null
                : jsonMapper.writeValueAsString(event.required("payload"));

        repository.save(new RouteProjection(routeId, version, deleted, payload));
    }
}