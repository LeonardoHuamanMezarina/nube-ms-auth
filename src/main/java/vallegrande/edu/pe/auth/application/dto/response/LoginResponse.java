package vallegrande.edu.pe.auth.application.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class LoginResponse {
    private String token;
    private Long id;
    private String name;
    private String lastname;
    private String email;
    private String role;
    private Long tenantId;
    private String status;
    private String redirectTo; // Nueva ruta sugerida
    private List<String> modules; // Lista de módulos permitidos
}
