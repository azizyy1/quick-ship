package ma.azizi.expeditions.repository;

import ma.azizi.expeditions.entity.ShipmentNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentNotificationRepository
        extends JpaRepository<ShipmentNotification, Long> {
}