package ma.azizi.expeditions.repository;

import ma.azizi.expeditions.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
}