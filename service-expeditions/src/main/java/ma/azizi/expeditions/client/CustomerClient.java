package ma.azizi.expeditions.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

@FeignClient(name = "service-clients", url = "${services.clients.url}")
public interface CustomerClient {

    @GetMapping("/api/customers/{id}")
    CustomerDetails getCustomer(@PathVariable("id") Long id);

    record CustomerDetails(
            Long id,
            String nom,
            String email,
            BigDecimal encours,
            BigDecimal plafondCredit,
            String statut
    ) {
    }
}