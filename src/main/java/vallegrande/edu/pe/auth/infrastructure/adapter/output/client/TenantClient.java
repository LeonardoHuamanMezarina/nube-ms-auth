package vallegrande.edu.pe.auth.infrastructure.adapter.output.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class TenantClient {
    private final WebClient webClient;

    public TenantClient(WebClient.Builder builder) {
        // El microservicio de tenants corre en el puerto 8081
        this.webClient = builder.baseUrl("http://localhost:8081/api/v1/tenants").build();
    }

    public Mono<Void> activateTenant(Long tenantId) {
        return webClient.patch()
                .uri("/active/{id}", tenantId)
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorResume(e -> {
                    System.err.println("Error activando tenant: " + e.getMessage());
                    return Mono.empty(); // No bloqueamos la creación del usuario si falla la activación
                });
    }
}
