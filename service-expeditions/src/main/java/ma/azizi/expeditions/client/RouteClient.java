package ma.azizi.expeditions.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "service-reseau", url = "${services.reseau.url}")
public interface RouteClient {

    @GetMapping("/api/routes/{id}")
    void getRoute(@PathVariable("id") Long id);
}