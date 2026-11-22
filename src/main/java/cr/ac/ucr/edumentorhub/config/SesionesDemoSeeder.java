package cr.ac.ucr.edumentorhub.config;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cr.ac.ucr.edumentorhub.entity.EstadoTutoria;
import cr.ac.ucr.edumentorhub.entity.Materia;
import cr.ac.ucr.edumentorhub.entity.Modalidad;
import cr.ac.ucr.edumentorhub.entity.SesionTutoria;
import cr.ac.ucr.edumentorhub.entity.Usuario;
import cr.ac.ucr.edumentorhub.repository.MateriaRepository;
import cr.ac.ucr.edumentorhub.repository.SesionTutoriaRepository;
import cr.ac.ucr.edumentorhub.repository.UsuarioRepository;

/**
 * Sesiones de DEMOSTRACIÓN para probar el frontend sin esperar a que llegue la hora de una tutoría.
 * Se crean una sola vez (si la tabla está vacía) con fechas relativas al momento del arranque.
 * Se insertan directo en la base de datos: no pasan por las reglas del servicio.
 */
@Component
@Profile("!test")
@Order(3)
public class SesionesDemoSeeder implements CommandLineRunner {

    private final SesionTutoriaRepository sesionRepository;
    private final UsuarioRepository usuarioRepository;
    private final MateriaRepository materiaRepository;
    private final Clock clock;

    public SesionesDemoSeeder(SesionTutoriaRepository sesionRepository, UsuarioRepository usuarioRepository,
            MateriaRepository materiaRepository, Clock clock) {
        this.sesionRepository = sesionRepository;
        this.usuarioRepository = usuarioRepository;
        this.materiaRepository = materiaRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (sesionRepository.count() > 0) {
            return;
        }
        Usuario luis = usuarioRepository.findByEmail("tutor1@ucr.ac.cr").orElse(null);
        Usuario ana = usuarioRepository.findByEmail("estudiante1@ucr.ac.cr").orElse(null);
        Usuario diego = usuarioRepository.findByEmail("estudiante2@ucr.ac.cr").orElse(null);
        Materia software = materiaRepository.findAll().stream()
                .filter(m -> m.getCodigoMateria().equals("IF0009")).findFirst().orElse(null);
        Materia bases = materiaRepository.findAll().stream()
                .filter(m -> m.getCodigoMateria().equals("IF0005")).findFirst().orElse(null);
        if (luis == null || ana == null || diego == null || software == null || bases == null) {
            return;
        }

        LocalDateTime ahora = LocalDateTime.now(clock).truncatedTo(ChronoUnit.MINUTES);

        // Ya terminó: Ana puede evaluarla
        crear(ana, luis, bases, ahora.minusDays(2), 60, EstadoTutoria.COMPLETED,
                "Repasamos normalización hasta 3FN y practicamos consultas con JOIN.");
        // Empezó hace 10 minutos: Luis puede iniciarla
        crear(ana, luis, software, ahora.minusMinutes(10), 60, EstadoTutoria.CONFIRMED, null);
        // Dentro de 2 días: Ana todavía puede cancelarla
        crear(ana, luis, software, ahora.plusDays(2), 60, EstadoTutoria.CONFIRMED, null);
        // Solicitud pendiente de Diego: Luis puede aceptarla o rechazarla
        crear(diego, luis, bases, ahora.plusDays(3), 90, EstadoTutoria.REQUESTED, null);
    }

    private void crear(Usuario estudiante, Usuario tutor, Materia materia, LocalDateTime inicio, int minutos,
            EstadoTutoria estado, String resumen) {
        SesionTutoria sesion = new SesionTutoria(estudiante, tutor, materia, inicio, minutos,
                inicio.plusMinutes(minutos), Modalidad.VIRTUAL, estado);
        sesion.setResumenTrabajo(resumen);
        sesionRepository.save(sesion);
    }
}
