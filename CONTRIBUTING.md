# Guía de contribución

1. Prohibido hacer commit directo a `main` (excepto el commit inicial del repositorio).
2. Una rama por REQ: `feature/<nombre-corto>` (ej. `feature/auth-login`, `feature/reserva-ui`).
3. Commits semánticos con el REQ en el alcance:
   `feat(REQ-08): agregar interceptor HTTP con encabezado Authorization`
   Tipos: `feat`, `fix`, `docs`, `test`, `refactor`, `chore`, `build`.
4. Todo cambio entra a `main` por Pull Request con al menos una aprobación de un compañero.
5. Antes de abrir el PR: `mvn test`.
