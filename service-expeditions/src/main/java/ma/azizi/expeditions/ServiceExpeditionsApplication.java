package ma.azizi.expeditions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ServiceExpeditionsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceExpeditionsApplication.class, args);
	}
}