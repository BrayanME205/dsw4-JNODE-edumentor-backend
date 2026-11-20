package cr.ac.ucr.edumentorhub.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cr.ac.ucr.edumentorhub.dto.materia.MateriaRequest;
import cr.ac.ucr.edumentorhub.dto.materia.MateriaResponse;
import cr.ac.ucr.edumentorhub.entity.Materia;
import cr.ac.ucr.edumentorhub.exception.ConflictoException;
import cr.ac.ucr.edumentorhub.exception.RecursoNoEncontradoException;
import cr.ac.ucr.edumentorhub.repository.MateriaRepository;

@ExtendWith(MockitoExtension.class)
class MateriaServiceTest {

    @Mock
    private MateriaRepository materiaRepository;

    @InjectMocks
    private MateriaService materiaService;

    private final MateriaRequest request = new MateriaRequest("IF0009", "Desarrollo de Software IV",
            "Informática Empresarial", 4);

    @Test
    void crearConCodigoDuplicadoLanzaConflicto() {
        when(materiaRepository.existsByCodigoMateria("IF0009")).thenReturn(true);

        assertThrows(ConflictoException.class, () -> materiaService.crear(request));
        verify(materiaRepository, never()).save(any());
    }

    @Test
    void crearConCodigoNuevoGuardaLaMateria() {
        when(materiaRepository.existsByCodigoMateria("IF0009")).thenReturn(false);
        when(materiaRepository.save(any(Materia.class))).thenAnswer(inv -> inv.getArgument(0));

        MateriaResponse respuesta = materiaService.crear(request);

        assertEquals("IF0009", respuesta.codigoMateria());
        assertEquals(4, respuesta.creditos());
    }

    @Test
    void obtenerUnaMateriaInexistenteLanza404() {
        when(materiaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> materiaService.obtener(99L));
    }
}
