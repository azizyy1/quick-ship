package ma.azizi.expeditions.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ShipmentCreatedConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(ShipmentCreatedConsumer.class);

    @RabbitListener(queues = "shipment.created")
    public void onShipmentCreated(String shipmentId) {
        log.info("Message RabbitMQ reçu pour l’expédition {}", shipmentId);
    }
}