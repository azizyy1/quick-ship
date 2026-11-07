package ma.azizi.expeditions.controller;

import java.util.List;
import ma.azizi.expeditions.entity.ShipmentNotification;
import ma.azizi.expeditions.repository.ShipmentNotificationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class ShipmentNotificationController {

    private final ShipmentNotificationRepository notifications;

    public ShipmentNotificationController(ShipmentNotificationRepository notifications) {
        this.notifications = notifications;
    }

    @GetMapping
    public List<ShipmentNotification> findAll() {
        return notifications.findAll();
    }
}