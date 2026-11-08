package ma.azizi.expeditions.messaging;

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

    public ShipmentMessaging(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
}