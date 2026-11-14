package ma.azizi.expeditions.controller;

import java.util.List;
import ma.azizi.expeditions.entity.ShipmentStatusNotification;
import ma.azizi.expeditions.repository.ShipmentStatusNotificationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shipment-notifications")
public class ShipmentNotificationController {

    private final ShipmentStatusNotificationRepository notifications;

    public ShipmentNotificationController(
            ShipmentStatusNotificationRepository notifications
    ) {
        this.notifications = notifications;
    }

    @GetMapping
    public List<ShipmentStatusNotification> findByCustomer(
            @RequestParam("customerId") Long customerId
    ) {
        return notifications.findByCustomerId(customerId);
    }
}