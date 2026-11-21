package cr.ac.ucr.edumentorhub.config;

import java.time.DayOfWeek;
import java.time.LocalTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cr.ac.ucr.edumentorhub.entity.DisponibilidadTutor;
import cr.ac.ucr.edumentorhub.entity.Usuario;
import cr.ac.ucr.edumentorhub.repository.DisponibilidadTutorRepository;
import cr.ac.ucr.edumentorhub.repository.UsuarioRepository;

/**
 * Horarios de demostración para los tutores semilla (corre después de DataSeeder).
 * Solo crea bloques si el tutor todavía no tiene ninguno.
 */
@Component
@Profile("!test")
@Order(2)
public class DisponibilidadSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final DisponibilidadTutorRepository disponibilidadRepository;

    public DisponibilidadSeeder(UsuarioRepository usuarioRepository,
            DisponibilidadTutorRepository disponibilidadRepository) {
        this.usuarioRepository = usuarioRepository;
        this.disponibilidadRepository = disponibilidadRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        usuarioRepository.findByEmail("tutor1@ucr.ac.cr").ifPresent(tutor -> {
            if (disponibilidadRepository.findByTutorIdAndEstaActivoTrue(tutor.getId()).isEmpty()) {
                // Luis: entre semana, de 8:00 a 20:00 (facilita las pruebas a cualquier hora del día)
                for (DayOfWeek dia : new DayOfWeek[] { DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
                        DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY }) {
                    crear(tutor, dia, 8, 20);
                }
            }
        });
        usuarioRepository.findByEmail("tutor2@ucr.ac.cr").ifPresent(tutor -> {
            if (disponibilidadRepository.findByTutorIdAndEstaActivoTrue(tutor.getId()).isEmpty()) {
                // María: tardes y sábado en la mañana
                crear(tutor, DayOfWeek.MONDAY, 13, 17);
                crear(tutor, DayOfWeek.WEDNESDAY, 13, 17);
                crear(tutor, DayOfWeek.SATURDAY, 8, 12);
            }
        });
    }

    private void crear(Usuario tutor, DayOfWeek dia, int horaInicio, int horaFin) {
        disponibilidadRepository.save(new DisponibilidadTutor(tutor, dia,
                LocalTime.of(horaInicio, 0), LocalTime.of(horaFin, 0)));
    }
}
