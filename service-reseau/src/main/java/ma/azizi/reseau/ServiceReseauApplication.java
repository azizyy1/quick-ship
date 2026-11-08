package ma.azizi.reseau;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ServiceReseauApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceReseauApplication.class, args);
	}
}