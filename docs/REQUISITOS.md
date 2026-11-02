# Backlog de requisitos (REQ) — EduMentor Hub

Equipo: Dev 1, Dev 2, Dev 3 (reemplazar por los nombres reales).

| REQ | Descripción | Repo | Responsable | Depende de | Sprint |
|-----|-------------|------|-------------|------------|--------|
| REQ-00A | Arquitectura base backend | BE | Dev 1 | — | 1 |
| REQ-00B | Arquitectura base frontend | FE | Dev 2 | — | 1 |
| REQ-00C | Gobernanza (README, plantillas PR, protección de main, carpeta docs) | BE+FE | Dev 3 | — | 1 |
| REQ-01 | ERD | BE (docs) | Dev 1 | REQ-00C | 1 |
| REQ-02 | Diagrama de clases UML | BE (docs) | Dev 2 | REQ-01 | 1 |
| REQ-03 | Especificación OpenAPI 3.0 | BE (docs) | Dev 3 | REQ-01, REQ-02 | 1 |
| REQ-04 | Entidad Usuario + enum Rol + repositorio | BE | Dev 1 | REQ-01 | 1 |
| REQ-05 | Manejo global de errores RFC 7807 | BE | Dev 3 | REQ-00A | 1 |
| REQ-06 | Autenticación y autorización JWT (Spring Security) | BE | Dev 1 | REQ-04, REQ-05 | 2 |
| REQ-07 | Materia: entidad + CRUD | BE | Dev 1 | REQ-06 | 2 |
| REQ-08 | Login, interceptor HTTP, guardas funcionales y layout | FE | Dev 3 | REQ-06 | 2 |
| REQ-09 | DisponibilidadTutor: entidad + servicio + endpoints | BE | Dev 2 | REQ-06 | 2 |
| REQ-10 | SesionTutoria: solicitud, aceptar/rechazar, prevención de traslapes | BE | Dev 2 | REQ-07, REQ-09 | 3 |
| REQ-11 | Cancelación (2 h), IN_SESSION y COMPLETED | BE | Dev 2 | REQ-10 | 3 |
| REQ-12 | Pruebas JUnit 5 + Mockito de REQ-10 y REQ-11 | BE | Dev 2 | REQ-10, REQ-11 | 3 |
| REQ-13 | RecursoEstudio: entidad + endpoints | BE | Dev 3 | REQ-10 | 3 |
| REQ-14 | EvaluacionTutoria + promedio del tutor + estado REVIEWED + pruebas | BE | Dev 3 | REQ-11 | 3 |
| REQ-15 | Catálogo de materias | FE | Dev 1 | REQ-07, REQ-08 | 3 |
| REQ-16 | Gestión de disponibilidad y matriz de horarios | FE | Dev 2 | REQ-09, REQ-08 | 3 |
| REQ-17 | Formulario reactivo de reserva y acciones por estado | FE | Dev 2 | REQ-10, REQ-11, REQ-15 | 4 |
| REQ-18 | Recursos y evaluación de sesión | FE | Dev 3 | REQ-13, REQ-14 | 4 |
| REQ-19 | Internacionalización ES/EN | FE | Dev 1 | REQ-15 a REQ-18 | 4 |
| REQ-20 | Accesibilidad (Lighthouse ≥ 90) | FE | Dev 3 | REQ-15 a REQ-19 | 4 |
| REQ-21 | Datos semilla: usuarios por rol para pruebas | BE | Dev 1 | REQ-04 | 2 |
| REQ-22 | Integración final, README y documento de entrega | BE+FE | Dev 2 | Todos | 4 |

Sprints: S1 1–8 nov · S2 9–15 nov · S3 16–22 nov · S4 23–29 nov · Entrega y defensas: lunes 30 nov, 12:59.
