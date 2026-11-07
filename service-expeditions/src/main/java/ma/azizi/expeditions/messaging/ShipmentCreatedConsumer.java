package ma.azizi.expeditions.messaging;

import ma.azizi.expeditions.entity.ShipmentNotification;
import ma.azizi.expeditions.repository.ShipmentNotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ShipmentCreatedConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(ShipmentCreatedConsumer.class);

    private final ShipmentNotificationRepository notifications;

    public ShipmentCreatedConsumer(ShipmentNotificationRepository notifications) {
        this.notifications = notifications;
    }

    @RabbitListener(queues = "shipment.created")
    public void onShipmentCreated(String shipmentId) {
        notifications.save(new ShipmentNotification(Long.valueOf(shipmentId)));
        log.info("Notification enregistrée pour l’expédition {}", shipmentId);
    }
}