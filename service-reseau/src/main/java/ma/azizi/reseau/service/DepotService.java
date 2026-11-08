package ma.azizi.reseau.service;

import ma.azizi.reseau.dto.DepotRequest;
import ma.azizi.reseau.dto.DepotResponse;
import ma.azizi.reseau.entity.Depot;
import ma.azizi.reseau.repository.DepotRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DepotService {

    private final DepotRepository depotRepository;

    public DepotService(DepotRepository depotRepository) {
        this.depotRepository = depotRepository;
    }

    public List<DepotResponse> findAll() {
        return depotRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public DepotResponse findById(Long id) {
        return toResponse(getDepot(id));
    }

    @Transactional
    public DepotResponse create(DepotRequest request) {
        Depot depot = new Depot();
        applyRequest(depot, request);

        return toResponse(depotRepository.save(depot));
    }

    @Transactional
    public DepotResponse update(Long id, DepotRequest request) {
        Depot depot = getDepot(id);
        applyRequest(depot, request);

        return toResponse(depotRepository.save(depot));
    }

    @Transactional
    public void delete(Long id) {
        Depot depot = getDepot(id);
        depotRepository.delete(depot);
    }

    private Depot getDepot(Long id) {
        return depotRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Dépôt introuvable : " + id
                ));
    }

    private void applyRequest(Depot depot, DepotRequest request) {
        depot.setNom(request.nom().trim());
        depot.setVille(request.ville().trim());
        depot.setCapacite(request.capacite());
    }

    private DepotResponse toResponse(Depot depot) {
        return new DepotResponse(
                depot.getId(),
                depot.getNom(),
                depot.getVille(),
                depot.getCapacite()
        );
    }
}