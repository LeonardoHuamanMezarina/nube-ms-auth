package vallegrande.edu.pe.auth.infrastructure.adapter.output.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.auth.domain.model.User;
import vallegrande.edu.pe.auth.domain.port.out.UserRepositoryPort;

@Repository
@RequiredArgsConstructor
public class R2dbcUserRepository implements UserRepositoryPort {

    private final SpringDataUserRepository repository;

    @Override
    public Mono<User> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public Flux<User> findAll() {
        return repository.findAll();
    }

    @Override
    public Mono<User> save(User user) {
        return repository.save(user);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Mono<Boolean> existsByDni(String dni) {
        return repository.existsByDni(dni);
    }

    @Override
    public Flux<User> findByTenantId(Long tenantId) {
        return repository.findByTenantId(tenantId);
    }
}

interface SpringDataUserRepository extends ReactiveCrudRepository<User, Long> {
    Mono<User> findByEmail(String email);
    Mono<Boolean> existsByEmail(String email);
    Mono<Boolean> existsByDni(String dni);
    Flux<User> findByTenantId(Long tenantId);
}
