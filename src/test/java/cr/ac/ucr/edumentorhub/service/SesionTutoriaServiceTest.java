package cr.ac.ucr.edumentorhub.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import cr.ac.ucr.edumentorhub.dto.sesion.FinalizarSesionRequest;
import cr.ac.ucr.edumentorhub.dto.sesion.SesionResponse;
import cr.ac.ucr.edumentorhub.dto.sesion.SolicitudSesionRequest;
import cr.ac.ucr.edumentorhub.entity.EstadoTutoria;
import cr.ac.ucr.edumentorhub.entity.Materia;
import cr.ac.ucr.edumentorhub.entity.Modalidad;
import cr.ac.ucr.edumentorhub.entity.Rol;
import cr.ac.ucr.edumentorhub.entity.SesionTutoria;
import cr.ac.ucr.edumentorhub.entity.Usuario;
import cr.ac.ucr.edumentorhub.exception.ReglaNegocioException;
import cr.ac.ucr.edumentorhub.exception.TraslapeHorarioException;
import cr.ac.ucr.edumentorhub.repository.DisponibilidadTutorRepository;
import cr.ac.ucr.edumentorhub.repository.MateriaRepository;
import cr.ac.ucr.edumentorhub.repository.SesionTutoriaRepository;
import cr.ac.ucr.edumentorhub.repository.UsuarioRepository;

/**
 * Pruebas de las reglas de negocio de SesionTutoriaService, sin base de datos ni hora real.
 * La hora "actual" está fija en el 20 de noviembre de 2026 a las 12:00.
 */
@ExtendWith(MockitoExtension.class)
class SesionTutoriaServiceTest {

    private static final String EMAIL_ESTUDIANTE = "ana@ucr.ac.cr";
    private static final String EMAIL_TUTOR = "luis@ucr.ac.cr";

    @Mock
    private SesionTutoriaRepository sesionRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private MateriaRepository materiaRepository;
    @Mock
    private DisponibilidadTutorRepository disponibilidadRepository;
    @Mock
    private UsuarioActualService usuarioActualService;

    private final Clock reloj = Clock.fixed(Instant.parse("2026-11-20T12:00:00Z"), ZoneOffset.UTC);

    private SesionTutoriaService servicio;
    private Usuario estudiante;
    private Usuario tutor;
    private Materia materia;

    @BeforeEach
    void preparar() {
        servicio = new SesionTutoriaService(sesionRepository, usuarioRepository, materiaRepository,
                disponibilidadRepository, usuarioActualService, reloj);

        estudiante = new Usuario("C20001", EMAIL_ESTUDIANTE, "hash", "Ana", "Mora", Rol.ESTUDIANTE);
        ReflectionTestUtils.setField(estudiante, "id", 1L);
        tutor = new Usuario("B10001", EMAIL_TUTOR, "hash", "Luis", "Rojas", Rol.TUTOR);
        ReflectionTestUtils.setField(tutor, "id", 2L);
        materia = new Materia("IF0009", "Desarrollo de Software IV", "Informática Empresarial", 4);
        ReflectionTestUtils.setField(materia, "id", 3L);
    }

    private SesionTutoria sesion(EstadoTutoria estado, LocalDateTime inicio) {
        SesionTutoria sesion = new SesionTutoria(estudiante, tutor, materia, inicio, 60, inicio.plusMinutes(60),
                Modalidad.VIRTUAL, estado);
        ReflectionTestUtils.setField(sesion, "id", 10L);
        when(sesionRepository.findConDetalleById(10L)).thenReturn(Optional.of(sesion));
        return sesion;
    }

    // ---------- 1 y 2: aceptación con y sin traslape ----------

    @Test
    void aceptarSinTraslapeDejaLaSesionConfirmada() {
        SesionTutoria solicitud = sesion(EstadoTutoria.REQUESTED, LocalDateTime.of(2026, 11, 25, 15, 0));
        when(sesionRepository.existsByTutorIdAndEstadoInAndFechaHoraLessThanAndFechaHoraFinGreaterThan(
                eq(2L), anyCollection(), any(), any())).thenReturn(false);
        when(sesionRepository.existsByEstudianteIdAndEstadoInAndFechaHoraLessThanAndFechaHoraFinGreaterThan(
                eq(1L), anyCollection(), any(), any())).thenReturn(false);
        when(sesionRepository.save(solicitud)).thenReturn(solicitud);

        SesionResponse respuesta = servicio.aceptar(10L, EMAIL_TUTOR);

        assertEquals(EstadoTutoria.CONFIRMED, respuesta.estado());
    }

    @Test
    void aceptarConTraslapeRechazaLaSesionYLanzaExcepcion() {
        SesionTutoria solicitud = sesion(EstadoTutoria.REQUESTED, LocalDateTime.of(2026, 11, 25, 15, 0));
        when(sesionRepository.existsByTutorIdAndEstadoInAndFechaHoraLessThanAndFechaHoraFinGreaterThan(
                eq(2L), anyCollection(), any(), any())).thenReturn(true);

        assertThrows(TraslapeHorarioException.class, () -> servicio.aceptar(10L, EMAIL_TUTOR));

        assertEquals(EstadoTutoria.REJECTED, solicitud.getEstado());
        verify(sesionRepository).save(solicitud);
    }

    // ---------- 3 y 4: cancelación con y sin 2 horas de anticipación ----------

    @Test
    void cancelarConTresHorasDeAnticipacionFunciona() {
        SesionTutoria confirmada = sesion(EstadoTutoria.CONFIRMED, LocalDateTime.of(2026, 11, 20, 15, 0));
        when(sesionRepository.save(confirmada)).thenReturn(confirmada);

        SesionResponse respuesta = servicio.cancelar(10L, EMAIL_ESTUDIANTE);

        assertEquals(EstadoTutoria.CANCELLED, respuesta.estado());
    }

    @Test
    void cancelarConUnaHoraDeAnticipacionLanzaReglaNegocio() {
        sesion(EstadoTutoria.CONFIRMED, LocalDateTime.of(2026, 11, 20, 13, 0));

        ReglaNegocioException error = assertThrows(ReglaNegocioException.class,
                () -> servicio.cancelar(10L, EMAIL_ESTUDIANTE));

        assertEquals("La cancelación requiere al menos 2 horas de anticipación", error.getMessage());
        verify(sesionRepository, never()).save(any());
    }

    // ---------- 5: solicitud fuera de la disponibilidad del tutor ----------

    @Test
    void solicitarFueraDeLaDisponibilidadDelTutorLanzaReglaNegocio() {
        when(usuarioActualService.obtener(EMAIL_ESTUDIANTE)).thenReturn(estudiante);
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(tutor));
        when(materiaRepository.findById(3L)).thenReturn(Optional.of(materia));
        when(disponibilidadRepository.existeBloqueQueCubre(eq(2L), any(), any(), any())).thenReturn(false);
        SolicitudSesionRequest solicitud = new SolicitudSesionRequest(2L, 3L,
                LocalDateTime.of(2026, 11, 25, 21, 0), 60, Modalidad.VIRTUAL);

        assertThrows(ReglaNegocioException.class, () -> servicio.solicitar(EMAIL_ESTUDIANTE, solicitud));
        verify(sesionRepository, never()).save(any());
    }

    // ---------- 6: transición inválida en la máquina de estados ----------

    @Test
    void finalizarUnaSesionQueNoHaIniciadoLanzaReglaNegocio() {
        sesion(EstadoTutoria.CONFIRMED, LocalDateTime.of(2026, 11, 20, 11, 0));

        assertThrows(ReglaNegocioException.class,
                () -> servicio.finalizar(10L, EMAIL_TUTOR, new FinalizarSesionRequest("Repaso de JPA")));
        verify(sesionRepository, never()).save(any());
    }
}
