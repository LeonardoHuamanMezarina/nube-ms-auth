package vallegrande.edu.pe.auth.domain.port.in;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.auth.application.dto.request.CreateAdminRequest;
import vallegrande.edu.pe.auth.application.dto.request.LoginRequest;
import vallegrande.edu.pe.auth.application.dto.response.LoginResponse;
import vallegrande.edu.pe.auth.application.dto.response.UserResponse;

import java.util.List;

public interface AuthUseCase {
    Mono<LoginResponse> login(LoginRequest request);

    Mono<UserResponse> findById(Long id);

    Flux<UserResponse> findAll();

    Mono<UserResponse> create(CreateAdminRequest request);

    Mono<UserResponse> update(Long id, vallegrande.edu.pe.auth.application.dto.request.UpdateUserRequest request);

    Mono<Void> delete(Long id);

    Mono<UserResponse> restore(Long id);

    Mono<List<Long>> findUsedTenantIds();
}
