package ma.azizi.expeditions.messaging;

import java.nio.charset.StandardCharsets;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.event.TransactionalEventListener;

@Configuration(proxyBeanMethods = false)
public class ShipmentMessaging {

    private static final String SHIPMENT_CREATED_QUEUE = "shipment.created";

    private static final org.slf4j.Logger log =
            org.slf4j.LoggerFactory.getLogger(ShipmentMessaging.class);

    @Bean
    Queue shipmentCreatedQueue() {
        return new Queue(SHIPMENT_CREATED_QUEUE, true);
    }

    @TransactionalEventListener
    public void publishShipmentCreated(ShipmentCreated event) {
        try {
            rabbitTemplate.convertAndSend(
                    "", SHIPMENT_CREATED_QUEUE, event.shipmentId().toString());
        } catch (AmqpException exception) {
            log.error("Échec de publication pour l’expédition {}", event.shipmentId(), exception);
        }
    }

    private final RabbitTemplate rabbitTemplate;

    private static final String SHIPMENT_STATUS_QUEUE = "shipment.status.changed";

    @Bean
    public Queue shipmentStatusChangedQueue() {
        return new Queue(SHIPMENT_STATUS_QUEUE, true);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishShipmentStatusChanged(ShipmentStatusChanged event) {
        String json = """
            {"shipmentId":%d,"customerId":%d,"status":"%s"}
            """.formatted(
                event.shipmentId(), event.customerId(), event.status().name());

        Message message = MessageBuilder
                .withBody(json.getBytes(StandardCharsets.UTF_8))
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setContentEncoding(StandardCharsets.UTF_8.name())
                .build();

        try {
            rabbitTemplate.send("", SHIPMENT_STATUS_QUEUE, message);
        } catch (AmqpException exception) {
            log.error("Échec de publication du statut {} pour l’expédition {}",
                    event.status(), event.shipmentId(), exception);
        }
    }

    public ShipmentMessaging(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
}