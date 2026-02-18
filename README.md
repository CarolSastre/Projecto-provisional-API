# Projecto-provicional-API

API Spring Boot para la aplicación de escritorio Web Familia.

## Endpoints principales

- `POST /api/usuarios/login` — Login por DNI + contraseña, devuelve `{ usuario, token }` (JWT).
- `POST /api/usuarios/register` — Registro/alta de usuario (alumno/profesor/admin).
- `POST /api/usuarios/login-nfc` — Login por NFC.
- `POST /api/usuarios/asignar-nfc` — Asignar UID NFC a usuario (JWT requerido).
- `POST /api/usuarios/registrar-entrada-salida` — Registrar entrada/salida alterna por NFC.
- `POST /api/usuarios/registrar-entrada-salida-app` — Registrar entrada/salida asociada a la sesión de la app (sin NFC).
- `GET  /api/usuarios/registros` — Listado de registros de entrada/salida de un usuario.
- `POST /api/usuarios/actualizar` — Actualizar datos de usuario (dni, nombre, apellidos, gmail, rol, baja, verificado y `fotoPerfil`).

### Eventos de calendario

- `GET  /api/eventos` — Lista eventos de un mes (`year`, `month`), autenticado.
- `POST /api/eventos` — Crear evento (requiere rol PROFESOR o ADMIN).
- `PUT  /api/eventos/{id}` — Actualizar evento (PROFESOR/ADMIN).
- `DELETE /api/eventos/{id}` — Eliminar evento (PROFESOR/ADMIN).

### Acceso torniquete NFC

- `POST /api/acceso/validar` — Endpoint público para validar accesos físicos por NFC.

## Seguridad

`WebSecurityConfig` configura la API como Resource Server JWT:

- Permite sin autenticación: `/api/usuarios/login`, `/api/usuarios/register`, `/api/usuarios/login-nfc`, `/api/acceso/validar`.
- Resto de endpoints protegidos con JWT (scope basado en `Rol`).