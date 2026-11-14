package ma.azizi.expeditions.repository;

import java.util.List;
import ma.azizi.expeditions.entity.ShipmentStatusNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentStatusNotificationRepository
        extends JpaRepository<ShipmentStatusNotification, String> {

    List<ShipmentStatusNotification> findByCustomerId(Long customerId);
}