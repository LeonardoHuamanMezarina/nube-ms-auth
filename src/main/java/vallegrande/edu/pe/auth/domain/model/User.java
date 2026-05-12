package vallegrande.edu.pe.auth.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class User {

    @Id
    private Long id;

    @Column("tenant_id")
    private Long tenantId;

    private String name;
    private String lastname;
    private String email;
    private String phone;
    private String dni;

    @Column("password_hash")
    private String passwordHash;

    private String status;

    @Column("role_id")
    private Long roleId;

    // --- Lógica de Negocio (Diseño Táctico DDD) ---

    public boolean isActive() {
        return "ACTIVE".equals(this.status);
    }

    public void activate() {
        this.status = "ACTIVE";
    }

    public void deactivate() {
        this.status = "INACTIVE";
    }

    public boolean isSuperAdmin(String roleName) {
        return "SUPERADMIN".equals(roleName);
    }
}
