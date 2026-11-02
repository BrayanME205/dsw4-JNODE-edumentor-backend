# EduMentor Hub — Backend

API REST con **Spring Boot 3** (Java 17/21, Maven, Spring Data JPA, Spring Security + JWT).
Proyecto Integrador IF0009 Desarrollo de Software IV — UCR, II Ciclo 2026.

## Requisitos
- Java 17 o 21
- Maven 3.9+
- No requiere Docker. Base de datos H2 en archivo por defecto (SQL Server opcional).

## Ejecución local
```bash
mvn spring-boot:run     # http://localhost:8080
mvn test                # pruebas unitarias
```
Consola H2: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:file:./data/edumentor`, usuario `sa`, sin clave).

## Arquitectura en capas (`src/main/java/cr/ac/ucr/edumentorhub`)
`Controller → Service → Repository → Entity`

| Paquete | Responsabilidad |
|---------|-----------------|
| `controller` | Endpoints REST; sin lógica de negocio |
| `service` | Reglas de negocio (traslapes, cancelación 2 h, promedio) |
| `repository` | Spring Data JPA (evitar N+1: JOIN FETCH / @EntityGraph) |
| `entity` | Entidades JPA |
| `dto` / `mapper` | Contratos de entrada/salida y conversión |
| `security` / `config` | JWT, Spring Security, CORS |
| `exception` | `@RestControllerAdvice` con RFC 7807 |

Documentación de diseño en `docs/` (ERD, UML, `openapi.yaml`). Backlog y responsables: `docs/REQUISITOS.md`.
Reglas de Git: `CONTRIBUTING.md`.

## Credenciales de prueba
Se documentarán al completar REQ-21.
