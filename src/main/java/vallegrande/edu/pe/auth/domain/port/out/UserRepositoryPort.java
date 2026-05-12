package vallegrande.edu.pe.auth.domain.port.out;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.auth.domain.model.User;

public interface UserRepositoryPort {
    Mono<User> findById(Long id);
    Mono<User> findByEmail(String email);
    Flux<User> findAll();
    Mono<User> save(User user);
    Mono<Boolean> existsByEmail(String email);
    Mono<Boolean> existsByDni(String dni);
    Flux<User> findByTenantId(Long tenantId);
}
