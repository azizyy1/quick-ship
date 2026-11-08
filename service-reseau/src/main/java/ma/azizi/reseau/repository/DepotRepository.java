package ma.azizi.reseau.repository;

import ma.azizi.reseau.entity.Depot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepotRepository extends JpaRepository<Depot, Long> {
}