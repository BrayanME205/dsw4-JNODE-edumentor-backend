# Modelo Entidad-Relación // REQ-01 : Dev 1

Decisiones acordadas por el equipo:

1. Estudiante y tutor son roles de `usuario`, no entidades. `sesion_tutoria` referencia a `usuario` dos veces.
2. Enums guardados como texto.
3. Promedio del tutor (`promedio_calificacion`, `total_evaluaciones`) en `usuario`.
4. `fecha_hora_fin` en `sesion_tutoria`, calculada como `fecha_hora + duracion_minutos`.
5. Sin tabla intermedia tutor-materia.