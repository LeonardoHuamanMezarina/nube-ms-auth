package vallegrande.edu.pe.auth.application.dto.request;

import lombok.Data;

@Data
public class UpdateUserRequest {

    private String name;
    private String lastname;
    private String phone;
    private String dni;
    private String role; // Permite cambiar el rol
    private Long tenantId; // Permite reasignar a otra parroquia
}
