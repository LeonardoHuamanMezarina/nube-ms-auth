package vallegrande.edu.pe.auth.infrastructure.adapter.input.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.auth.application.dto.request.CreateAdminRequest;
import vallegrande.edu.pe.auth.domain.port.in.AuthUseCase;
import vallegrande.edu.pe.auth.application.dto.response.UserResponse;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final AuthUseCase authUseCase;

    @GetMapping("/users")
    public Mono<ResponseEntity<List<UserResponse>>> getAllUsers() {
        return authUseCase.findAll()
                .collectList()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/users/{id}")
    public Mono<ResponseEntity<UserResponse>> getUserById(@PathVariable Long id) {
        return authUseCase.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/users")
    public Mono<ResponseEntity<UserResponse>> createUser(@RequestBody CreateAdminRequest request) {
        return authUseCase.create(request)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/users/{id}")
    public Mono<ResponseEntity<UserResponse>> updateUser(@PathVariable Long id, @RequestBody vallegrande.edu.pe.auth.application.dto.request.UpdateUserRequest request) {
        return authUseCase.update(id, request)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/users/{id}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable Long id) {
        return authUseCase.delete(id)
                .thenReturn(ResponseEntity.noContent().build());
    }

    @PatchMapping("/users/{id}/restore")
    public Mono<ResponseEntity<UserResponse>> restoreUser(@PathVariable Long id) {
        return authUseCase.restore(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/users/tenants/used")
    public Mono<ResponseEntity<List<Long>>> getUsedTenantIds() {
        return authUseCase.findUsedTenantIds()
                .map(ResponseEntity::ok);
    }
}
