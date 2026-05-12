# Resumen Técnico: Microservicio de Identidad (Multi-Tenant)

Este microservicio gestiona la autenticación y el control de acceso basado en roles (RBAC) con soporte para multi-tenencia y permisos granulares.

## 1. Arquitectura de Base de Datos (5 Tablas)

Se implementó un esquema relacional que permite flexibilidad total para secretarios sin afectar la integridad de los párrocos.

1.  **`roles`**: Define los tipos de usuario (`SUPERADMIN`, `PARROCO`, `SECRETARIO`).
2.  **`permissions`**: Catálogo de módulos del sistema (ej: `sacramentos`, `fianzas-pagos`). Los nombres coinciden con las rutas de Angular.
3.  **`role_permissions`**: Define los permisos por defecto para cada rol.
4.  **`users`**: Información de los usuarios y su relación con un `tenant_id`.
5.  **`user_permissions`**: **(Tabla Clave)** Permite activar/desactivar módulos específicos para un usuario individual (especialmente Secretarios). Incluye una columna `status` (`ACTIVE`/`INACTIVE`) para borrado lógico.

## 2. Lógica de Permisos (RBAC Avanzado)

El sistema calcula los permisos del usuario siguiendo esta jerarquía:
- **Párroco/SuperAdmin**: Tienen sus permisos definidos por su Rol. Están protegidos y no pueden ser editados individualmente para garantizar el acceso total.
- **Secretario**: 
    - Al crearse, se sincronizan automáticamente todos los módulos de parroquia en su tabla `user_permissions` como `ACTIVE`.
    - El SuperAdmin puede cambiar el estado a `INACTIVE` para ocultar módulos específicos.
    - Si la tabla está vacía, hereda por defecto los permisos del Rol.

## 3. Seguridad y Auditoría
- **Borrado Lógico**: Los usuarios y los permisos no se eliminan físicamente; se marcan como `INACTIVE`.
- **JWT Dinámico**: El token incluye el `role`, `tenantId` y la lista de `modules` (permisos) para que el Frontend no necesite hacer peticiones extra.
- **Validación de Roles**: Los endpoints de administración solo permiten modificar permisos de usuarios con rol `SECRETARIO`.
