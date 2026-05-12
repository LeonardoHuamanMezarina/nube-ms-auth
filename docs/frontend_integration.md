# Guía de Integración Frontend (Angular)

Esta guía detalla cómo consumir los endpoints del microservicio de identidad y cómo manejar la seguridad en el Frontend.

## 1. Autenticación (`POST /api/auth/login`)

**Request Body:**
```json
{
  "email": "juan@parroquia.com",
  "password": "Password123!"
}
```

**Response Body (Exitoso):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "id": 3,
  "name": "Juan",
  "lastname": "Pérez",
  "email": "juan@parroquia.com",
  "role": "SECRETARIO",
  "tenantId": 1,
  "status": "ACTIVE",
  "redirectTo": "/1/sacramentos",
  "modules": ["sacramentos", "agenda-parroquial", "solicitudes", "fianzas-pagos", "voluntariado", "personas"]
}
```

### Implementación en Angular:
1.  **Redirección**: Usa el campo `redirectTo` para navegar al usuario inmediatamente después del login.
2.  **Sidebar/Menú**: Itera sobre el array `modules` para mostrar u ocultar los elementos del menú lateral.
3.  **Guards**: Implementa un `CanActivate` que verifique si la ruta actual está incluida en el array `modules` del usuario.

## 2. Gestión de Usuarios (Solo SuperAdmin)

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `POST` | `/api/admin/create` | Crea un Párroco o Secretario. |
| `PATCH` | `/api/admin/users/{id}` | Actualización parcial de datos. |
| `DELETE` | `/api/admin/users/{id}` | Desactiva un usuario (Soft Delete). |
| `PATCH` | `/api/admin/users/{id}/restore` | Reactiva un usuario. |

## 3. Gestión de Permisos Granulares (Para Secretarios)

Estos endpoints permiten al SuperAdmin personalizar qué ve cada secretario.

- **Listar todos los módulos**: `GET /api/admin/permissions`
- **Activar un módulo**: `POST /api/admin/users/{userId}/permissions/{permId}` (Cambia status a `ACTIVE`)
- **Desactivar un módulo**: `DELETE /api/admin/users/{userId}/permissions/{permId}` (Cambia status a `INACTIVE`)
- **Sincronizar base**: `POST /api/admin/users/{userId}/permissions/sync` (Copia los 6 módulos por defecto).

## 4. Notas de Seguridad
- El `tenantId` debe extraerse del token JWT para todas las peticiones a otros microservicios.
- Los nombres de los módulos en el array `modules` coinciden exactamente con los `path` definidos en `app.routes.ts`.
