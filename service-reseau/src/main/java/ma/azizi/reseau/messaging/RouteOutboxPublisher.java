package ma.azizi.reseau.messaging;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ma.azizi.reseau.entity.RouteOutboxEvent;
import ma.azizi.reseau.repository.RouteOutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Component
public class RouteOutboxPublisher {

    private static final Logger log = LoggerFactory.getLogger(RouteOutboxPublisher.class);
    private static final String TOPIC = "quickship.routes.events";

    private final RouteOutboxRepository outbox;
    private final KafkaTemplate<String, String> kafka;
    private final JsonMapper jsonMapper;

    public RouteOutboxPublisher(RouteOutboxRepository outbox,
                                KafkaTemplate<String, String> kafka,
                                JsonMapper jsonMapper) {
        this.outbox = outbox;
        this.kafka = kafka;
        this.jsonMapper = jsonMapper;
    }

    @Scheduled(fixedDelay = 3000)
    public void publishPendingEvents() {
        var pendingEvents = outbox.findTop20ByPublishedAtIsNullOrderByRouteIdAscRouteVersionAsc();

        for (RouteOutboxEvent event : pendingEvents) {
            try {
                RouteMessage message = new RouteMessage(
                        event.getEventId(),
                        event.getCorrelationId(),
                        event.getRouteId(),
                        event.getRouteVersion(),
                        event.getEventType(),
                        jsonMapper.readTree(event.getPayload())
                );

                kafka.send(TOPIC, event.getRouteId().toString(),
                                jsonMapper.writeValueAsString(message))
                        .get(10, TimeUnit.SECONDS);

                event.markPublished();
                outbox.save(event);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                return;
            } catch (ExecutionException | TimeoutException | RuntimeException exception) {
                log.warn("Publication de l’événement {} à réessayer",
                        event.getEventId(), exception);
                return;
            }
        }
    }

    private record RouteMessage(
            UUID eventId,
            UUID correlationId,
            Long routeId,
            long version,
            String eventType,
            JsonNode payload
    ) {
    }
}