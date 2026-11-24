package cr.ac.ucr.edumentorhub.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import cr.ac.ucr.edumentorhub.dto.evaluacion.EvaluacionRequest;
import cr.ac.ucr.edumentorhub.entity.EstadoTutoria;
import cr.ac.ucr.edumentorhub.entity.EvaluacionTutoria;
import cr.ac.ucr.edumentorhub.entity.Materia;
import cr.ac.ucr.edumentorhub.entity.Modalidad;
import cr.ac.ucr.edumentorhub.entity.Rol;
import cr.ac.ucr.edumentorhub.entity.SesionTutoria;
import cr.ac.ucr.edumentorhub.entity.Usuario;
import cr.ac.ucr.edumentorhub.exception.ConflictoException;
import cr.ac.ucr.edumentorhub.exception.ReglaNegocioException;
import cr.ac.ucr.edumentorhub.repository.EvaluacionTutoriaRepository;
import cr.ac.ucr.edumentorhub.repository.SesionTutoriaRepository;
import cr.ac.ucr.edumentorhub.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class EvaluacionTutoriaServiceTest {

    private static final String EMAIL_ESTUDIANTE = "ana@ucr.ac.cr";

    @Mock
    private EvaluacionTutoriaRepository evaluacionRepository;
    @Mock
    private SesionTutoriaRepository sesionRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private SesionTutoriaService sesionTutoriaService;

    private EvaluacionTutoriaService servicio;
    private Usuario tutor;

    @BeforeEach
    void preparar() {
        Clock reloj = Clock.fixed(Instant.parse("2026-11-20T12:00:00Z"), ZoneOffset.UTC);
        servicio = new EvaluacionTutoriaService(evaluacionRepository, sesionRepository, usuarioRepository,
                sesionTutoriaService, reloj);

        tutor = new Usuario("B10001", "luis@ucr.ac.cr", "hash", "Luis", "Rojas", Rol.TUTOR);
        // El tutor ya tiene 2 evaluaciones con promedio 4.00
        tutor.setPromedioCalificacion(new BigDecimal("4.00"));
        tutor.setTotalEvaluaciones(2);
    }

    private SesionTutoria sesionEnEstado(EstadoTutoria estado) {
        Usuario estudiante = new Usuario("C20001", EMAIL_ESTUDIANTE, "hash", "Ana", "Mora", Rol.ESTUDIANTE);
        Materia materia = new Materia("IF0009", "Desarrollo de Software IV", "Informática Empresarial", 4);
        LocalDateTime inicio = LocalDateTime.of(2026, 11, 19, 10, 0);
        SesionTutoria sesion = new SesionTutoria(estudiante, tutor, materia, inicio, 60, inicio.plusMinutes(60),
                Modalidad.VIRTUAL, estado);
        ReflectionTestUtils.setField(sesion, "id", 10L);
        when(sesionRepository.findConDetalleById(10L)).thenReturn(Optional.of(sesion));
        return sesion;
    }

    @Test
    void unaEvaluacionDeUnaEstrellaRecalculaElPromedioA3() {
        sesionEnEstado(EstadoTutoria.COMPLETED);
        when(evaluacionRepository.existsBySesionId(10L)).thenReturn(false);
        when(evaluacionRepository.save(any(EvaluacionTutoria.class))).thenAnswer(inv -> inv.getArgument(0));

        servicio.registrar(10L, EMAIL_ESTUDIANTE, new EvaluacionRequest(1, "Faltó tiempo para práctica"));

        // (4.00 * 2 + 1) / 3 = 3.00
        assertEquals(0, new BigDecimal("3.00").compareTo(tutor.getPromedioCalificacion()));
        assertEquals(3, tutor.getTotalEvaluaciones());
        verify(usuarioRepository).save(tutor);
        verify(sesionTutoriaService).marcarRevisada(10L);
    }

    @Test
    void evaluarUnaSesionQueNoEstaCompletadaLanzaReglaNegocio() {
        sesionEnEstado(EstadoTutoria.CONFIRMED);

        assertThrows(ReglaNegocioException.class,
                () -> servicio.registrar(10L, EMAIL_ESTUDIANTE, new EvaluacionRequest(5, null)));
        verify(evaluacionRepository, never()).save(any());
    }

    @Test
    void evaluarDosVecesLaMismaSesionLanzaConflicto() {
        sesionEnEstado(EstadoTutoria.COMPLETED);
        when(evaluacionRepository.existsBySesionId(10L)).thenReturn(true);

        assertThrows(ConflictoException.class,
                () -> servicio.registrar(10L, EMAIL_ESTUDIANTE, new EvaluacionRequest(5, null)));
        verify(evaluacionRepository, never()).save(any());
        verify(sesionTutoriaService, never()).marcarRevisada(any());
    }
}
