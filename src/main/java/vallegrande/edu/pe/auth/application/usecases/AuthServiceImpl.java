package vallegrande.edu.pe.auth.application.usecases;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.auth.domain.model.User;
import vallegrande.edu.pe.auth.domain.port.in.AuthUseCase;
import vallegrande.edu.pe.auth.domain.port.out.UserRepositoryPort;
import vallegrande.edu.pe.auth.application.dto.request.CreateAdminRequest;
import vallegrande.edu.pe.auth.application.dto.request.LoginRequest;
import vallegrande.edu.pe.auth.application.dto.response.LoginResponse;
import vallegrande.edu.pe.auth.application.dto.response.UserResponse;
import vallegrande.edu.pe.auth.infrastructure.security.JwtUtil;
import vallegrande.edu.pe.auth.infrastructure.adapter.output.client.TenantClient;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TenantClient tenantClient;

    /**
     * Procesa la autenticación del usuario.
     * Verifica credenciales, genera el token JWT y determina la ruta de redirección inicial.
     */
    @Override
    public Mono<LoginResponse> login(LoginRequest request) {
        return userRepositoryPort.findByEmail(request.getEmail())
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPasswordHash()))
                .switchIfEmpty(Mono.error(new RuntimeException("Credenciales inválidas")))
                .filter(User::isActive)
                .switchIfEmpty(Mono.error(new RuntimeException("Usuario inactivo")))
                .map(user -> {
                    String roleName = "USER"; 
                    String token = jwtUtil.generateToken(user.getEmail(), roleName, user.getTenantId(),
                            List.of("DASHBOARD", "SACRAMENTOS"));

                    String redirectTo = (user.getTenantId() != null)
                            ? "/" + user.getTenantId() + "/sacramentos"
                            : "/platform/home";

                    return LoginResponse.builder()
                            .token(token)
                            .email(user.getEmail())
                            .name(user.getName())
                            .lastname(user.getLastname())
                            .tenantId(user.getTenantId())
                            .role(roleName)
                            .redirectTo(redirectTo)
                            .status(user.getStatus())
                            .build();
                });
    }

    /**
     * Busca un usuario por su ID único.
     */
    @Override
    public Mono<UserResponse> findById(Long id) {
        return userRepositoryPort.findById(id)
                .map(this::toUserResponse);
    }

    /**
     * Lista todos los usuarios registrados en el sistema.
     */
    @Override
    public Flux<UserResponse> findAll() {
        return userRepositoryPort.findAll()
                .map(this::toUserResponse);
    }

    /**
     * Registra un nuevo administrador o párroco.
     * Valida formato de datos, verifica duplicados (Email/DNI), encripta contraseña
     * y activa automáticamente la parroquia si es un párroco.
     */
    @Override
    public Mono<UserResponse> create(CreateAdminRequest request) {
        validateUserRequest(request.getDni(), request.getPhone());
        
        return userRepositoryPort.existsByEmail(request.getEmail())
                .flatMap(exists -> exists
                        ? Mono.error(new RuntimeException("El email ya está registrado"))
                        : userRepositoryPort.existsByDni(request.getDni()))
                .flatMap(existsDni -> existsDni
                        ? Mono.error(new RuntimeException("El DNI ya está registrado"))
                        : Mono.just(request))
                .publishOn(Schedulers.boundedElastic())
                .map(req -> {
                    String rawPassword = (req.getPassword() != null && !req.getPassword().isBlank())
                            ? req.getPassword()
                            : req.getDni();

                    return User.builder()
                            .id(null)
                            .name(req.getName())
                            .lastname(req.getLastname())
                            .email(req.getEmail())
                            .dni(req.getDni())
                            .phone(req.getPhone())
                            .tenantId(req.getTenantId())
                            .status("ACTIVE")
                            .roleId("SUPERADMIN".equals(req.getRole()) ? 1L : 2L)
                            .passwordHash(passwordEncoder.encode(rawPassword))
                            .build();
                })
                .flatMap(userRepositoryPort::save)
                .flatMap(savedUser -> {
                    if (savedUser.getTenantId() != null && savedUser.getRoleId() == 2L) {
                        return tenantClient.activateTenant(savedUser.getTenantId())
                                .onErrorResume(e -> {
                                    System.err.println("Error al activar parroquia: " + e.getMessage());
                                    return Mono.empty();
                                })
                                .thenReturn(savedUser);
                    }
                    return Mono.just(savedUser);
                })
                .map(this::toUserResponse);
    }

    /**
     * Actualiza la información de un usuario existente.
     * Incluye validación de formato y verificación de DNI único si este cambia.
     */
    @Override
    public Mono<UserResponse> update(Long id, vallegrande.edu.pe.auth.application.dto.request.UpdateUserRequest request) {
        validateUserRequest(request.getDni(), request.getPhone());
        
        return userRepositoryPort.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Usuario no encontrado")))
                .flatMap(user -> {
                    if (request.getDni() != null && !request.getDni().equals(user.getDni())) {
                        return userRepositoryPort.existsByDni(request.getDni())
                                .flatMap(exists -> exists 
                                    ? Mono.error(new RuntimeException("El nuevo DNI ya está registrado"))
                                    : Mono.just(user));
                    }
                    return Mono.just(user);
                })
                .map(user -> {
                    user.setName(request.getName());
                    user.setLastname(request.getLastname());
                    user.setPhone(request.getPhone());
                    user.setDni(request.getDni());
                    user.setTenantId(request.getTenantId());
                    if (request.getRole() != null) {
                        user.setRoleId("SUPERADMIN".equals(request.getRole()) ? 1L : 2L);
                    }
                    return user;
                })
                .flatMap(userRepositoryPort::save)
                .map(this::toUserResponse);
    }

    /**
     * Realiza una eliminación lógica desactivando al usuario.
     */
    @Override
    public Mono<Void> delete(Long id) {
        return userRepositoryPort.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Usuario no encontrado")))
                .map(user -> {
                    user.setStatus("INACTIVE");
                    return user;
                })
                .flatMap(userRepositoryPort::save)
                .then();
    }

    /**
     * Restaura un usuario previamente desactivado poniéndolo en estado activo.
     */
    @Override
    public Mono<UserResponse> restore(Long id) {
        return userRepositoryPort.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Usuario no encontrado")))
                .map(user -> {
                    user.setStatus("ACTIVE");
                    return user;
                })
                .flatMap(userRepositoryPort::save)
                .map(this::toUserResponse);
    }

    /**
     * Valida que el DNI tenga 8 dígitos y que el teléfono empiece con 9 y tenga 9 dígitos.
     */
    private void validateUserRequest(String dni, String phone) {
        if (dni != null && !dni.isBlank()) {
            if (!dni.matches("\\d{8}")) {
                throw new RuntimeException("El DNI debe tener exactamente 8 números");
            }
        }
        if (phone != null && !phone.isBlank()) {
            if (!phone.matches("9\\d{8}")) {
                throw new RuntimeException("El teléfono debe empezar con 9 y tener 9 números");
            }
        }
    }

    /**
     * Obtiene la lista de IDs de parroquias que ya tienen un párroco asignado.
     */
    @Override
    public Mono<List<Long>> findUsedTenantIds() {
        return userRepositoryPort.findAll()
                .filter(u -> u.getTenantId() != null)
                .map(User::getTenantId)
                .distinct()
                .collectList();
    }

    /**
     * Mapea una entidad User a su objeto de respuesta DTO.
     */
    private UserResponse toUserResponse(User user) {
        String roleName = (user.getRoleId() != null && user.getRoleId() == 1L) ? "SUPERADMIN" : "PARROCO";
        
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .dni(user.getDni())
                .status(user.getStatus())
                .role(roleName)
                .tenantId(user.getTenantId())
                .build();
    }
}
