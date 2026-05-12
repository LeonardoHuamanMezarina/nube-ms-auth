package vallegrande.edu.pe.auth.infrastructure.adapter.input.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.auth.domain.port.in.AuthUseCase;
import vallegrande.edu.pe.auth.application.dto.request.LoginRequest;
import vallegrande.edu.pe.auth.application.dto.response.LoginResponse;
import vallegrande.edu.pe.auth.application.dto.response.UserResponse;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/login")
    public Mono<ResponseEntity<LoginResponse>> login(@RequestBody LoginRequest request) {
        return authUseCase.login(request)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @GetMapping("/users/{id}")
    public Mono<ResponseEntity<UserResponse>> getUser(@PathVariable Long id) {
        return authUseCase.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/users")
    public Mono<ResponseEntity<List<UserResponse>>> getAllUsers() {
        return authUseCase.findAll()
                .collectList()
                .map(ResponseEntity::ok);
    }
}
