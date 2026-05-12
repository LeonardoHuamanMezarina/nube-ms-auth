package vallegrande.edu.pe.auth.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateAdminRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "El apellido es obligatorio")
    private String lastname;

    @Email(message = "El email debe tener un formato válido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    private String password;

    private String confirmPassword;

    private String phone;
    private String dni;

    // Para /admin/init el rol lo asigna el server (SUPERADMIN)
    // Para /admin/create el rol lo elige el SUPERADMIN (PARROCO o SUPERADMIN)
    private String role;

    private Long tenantId; // NULL si es SUPERADMIN, requerido si es PARROCO
}
