package vallegrande.edu.pe.auth.application.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
@Builder
public class UserResponse {

    private Long id;
    private Long tenantId;
    private String name;
    private String lastname;
    private String email;
    private String phone;
    private String dni;
    private String status;
    private String role; // Solo el nombre del rol, no el objeto completo
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    // passwordHash nunca se incluye aquí
}
