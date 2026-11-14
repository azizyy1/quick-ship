package ma.azizi.expeditions.messaging;

import ma.azizi.expeditions.entity.ShipmentStatusNotification;
import ma.azizi.expeditions.repository.ShipmentStatusNotificationRepository;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class ShipmentStatusChangedConsumer {

    private final ShipmentStatusNotificationRepository notifications;
    private final ObjectMapper objectMapper;

    public ShipmentStatusChangedConsumer(
            ShipmentStatusNotificationRepository notifications,
            ObjectMapper objectMapper
    ) {
        this.notifications = notifications;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "shipment.status.changed")
    public void onShipmentStatusChanged(Message message) {
        ShipmentStatusChanged event =
                objectMapper.readValue(message.getBody(), ShipmentStatusChanged.class);

        notifications.save(new ShipmentStatusNotification(
                event.shipmentId(), event.customerId(), event.status()));
    }
}