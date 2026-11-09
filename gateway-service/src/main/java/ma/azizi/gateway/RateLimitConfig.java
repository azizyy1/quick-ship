package ma.azizi.gateway;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimitConfig {

    @Bean
    public KeyResolver clientIpKeyResolver() {
        return exchange -> Mono.justOrEmpty(exchange.getRequest().getRemoteAddress())
                .map(address -> address.getHostString());
    }
}