package ma.azizi.expeditions.repository;

import java.util.List;

import ma.azizi.expeditions.entity.TrackingEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackingEntryRepository extends JpaRepository<TrackingEntry, Long> {

    List<TrackingEntry> findByShipment_IdOrderByRecordedAtAscIdAsc(Long shipmentId);
}