# EduMentor Hub — Backend

API REST de tutorías académicas entre pares con **Spring Boot 3.3** (Java 21, Maven, Spring Data JPA, Spring Security + JWT, H2).
Proyecto Integrador IF0009 Desarrollo de Software IV — UCR, Recinto de Paraíso, II Ciclo 2026.

## Requisitos
- Java 21 (`java -version`)
- Maven 3.9+ (`mvn -version`)
- No requiere Docker. La base de datos H2 se crea sola en `./data/` (SQL Server es opcional).

## Ejecución local
```bash
mvn spring-boot:run          # API en http://localhost:8080/api
mvn test                     # pruebas unitarias (JUnit 5 + Mockito)
```
- Consola H2: `http://localhost:8080/h2-console` · JDBC URL `jdbc:h2:file:./data/edumentor` · usuario `sa` · sin contraseña.
- Para empezar con la base de datos vacía: detener la app y borrar la carpeta `data/`.
- Frontend Angular: ver el repositorio `dsw4-JNODE-edumentor-frontend` (se conecta a `http://localhost:8080/api` y el CORS ya permite `http://localhost:4200`).

## Credenciales de prueba
Se crean automáticamente al arrancar (`config/DataSeeder.java`). Contraseña común: **`Test1234!`**

| Rol | Correo | Nombre |
|-----|--------|--------|
| COORDINADOR | coordinador@ucr.ac.cr | Carmen Solano |
| TUTOR | tutor1@ucr.ac.cr | Luis Rojas |
| TUTOR | tutor2@ucr.ac.cr | María Vega |
| ESTUDIANTE | estudiante1@ucr.ac.cr | Ana Mora |
| ESTUDIANTE | estudiante2@ucr.ac.cr | Diego Brenes |

**Datos de demostración** (solo la primera vez, con fechas relativas al arranque):
- 5 materias: IF0001, IF0005, IF0009, MA0001, FS0001.
- Disponibilidad: Luis de lunes a viernes 8:00–20:00; María lunes y miércoles 13:00–17:00 y sábado 8:00–12:00.
- 4 sesiones para probar cada acción: una COMPLETED (Ana puede evaluarla), una CONFIRMED que ya empezó (Luis puede iniciarla), una CONFIRMED en 2 días (se puede cancelar) y una REQUESTED de Diego (Luis puede aceptarla o rechazarla).

## Endpoints (prefijo `/api`)
Contrato completo en `docs/openapi.yaml` (se puede abrir en https://editor.swagger.io).

| Método y ruta | Acceso | Descripción |
|---|---|---|
| `POST /auth/register` | público | Registro (solo ESTUDIANTE o TUTOR) |
| `POST /auth/login` | público | Devuelve el token JWT |
| `GET /materias`, `GET /materias/{id}` | público | Catálogo de materias |
| `POST /materias`, `PUT/DELETE /materias/{id}` | COORDINADOR | Administración del catálogo |
| `GET /tutores`, `GET /tutores/{id}` | público | Tutores con promedio y total de evaluaciones |
| `GET /tutores/{id}/evaluaciones` | público | Reseñas de un tutor |
| `GET /disponibilidades?tutorId=` | público | Horario semanal de un tutor |
| `POST /disponibilidades`, `PUT/DELETE /disponibilidades/{id}` | TUTOR | Gestión de sus bloques (DELETE = borrado lógico) |
| `POST /sesiones` | ESTUDIANTE | Solicitar sesión (REQUESTED) |
| `GET /sesiones/mias` | autenticado | Sesiones propias (coordinador: todas) |
| `GET /sesiones/{id}` | participantes o coordinador | Detalle |
| `PUT /sesiones/{id}/aceptar` · `/rechazar` | TUTOR | REQUESTED → CONFIRMED / REJECTED (409 si hay traslape) |
| `PUT /sesiones/{id}/cancelar` | ESTUDIANTE o TUTOR | → CANCELLED (422 con menos de 2 h) |
| `PUT /sesiones/{id}/iniciar` · `/finalizar` | TUTOR | → IN_SESSION → COMPLETED (finalizar recibe `{ "resumen": "..." }`) |
| `GET/POST /sesiones/{id}/recursos` | participantes | Recursos de estudio |
| `GET/POST /sesiones/{id}/evaluacion` | participantes / ESTUDIANTE | Evaluación 1–5 → REVIEWED y promedio del tutor |

Errores: siempre `application/problem+json` (RFC 7807) con `title`, `status`, `detail` e `instance`.
Códigos: 400 validación · 401 sin token · 403 sin permiso · 404 no existe · 409 conflicto o traslape · 422 regla de negocio · 500 inesperado.

### Prueba rápida con curl
```bash
# 1. Iniciar sesión como estudiante y copiar el token de la respuesta
curl -s -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" \
  -d '{"email":"estudiante1@ucr.ac.cr","password":"Test1234!"}'

# 2. Consultar mis sesiones con el token
curl -s http://localhost:8080/api/sesiones/mias -H "Authorization: Bearer <TOKEN>"
```

## Arquitectura en capas (`src/main/java/cr/ac/ucr/edumentorhub`)
`Controller → Service → Repository → Entity`

| Paquete | Responsabilidad |
|---------|-----------------|
| `controller` | Endpoints REST; reciben y devuelven DTOs; sin lógica de negocio |
| `service` | Reglas de negocio: traslapes, cancelación con 2 h, máquina de estados, promedio del tutor |
| `repository` | Spring Data JPA; los listados usan `@EntityGraph` para evitar consultas N+1 |
| `entity` | Entidades JPA (6) y enums |
| `dto` / `mapper` | Records de entrada/salida y su conversión desde las entidades |
| `security` | `JwtService`, `JwtAuthenticationFilter`, `CustomUserDetailsService` |
| `config` | Spring Security, CORS, `Clock`, datos semilla |
| `exception` | Excepciones de negocio y `GlobalExceptionHandler` (RFC 7807) |

## Pruebas
`mvn test` ejecuta:
- `SesionTutoriaServiceTest`: aceptar con y sin traslape, cancelar con 3 h y con 1 h, solicitud fuera de disponibilidad y transición inválida.
- `EvaluacionTutoriaServiceTest`: recálculo del promedio, sesión no completada y evaluación duplicada.
- `AuthServiceTest`, `MateriaServiceTest`, `JwtServiceTest`, `GlobalExceptionHandlerTest`, `UsuarioRepositoryTest` y la carga del contexto.

## Usar SQL Server (opcional)
En `src/main/resources/application.properties`, comentar las líneas de H2 y descomentar las de SQL Server con su usuario y contraseña. La base `edumentor` debe existir.

## Documentación de diseño (`docs/`)
- `ERD.md` y `diagrama-erd.*`: modelo entidad-relación.
- `UML.md` y `uml/*.mmd`: diagrama de clases por capas.
- `openapi.yaml`: especificación OpenAPI 3.0.
- `REQUISITOS.md`: backlog de requisitos y responsables. Reglas de Git en `CONTRIBUTING.md`.
