package cr.ac.ucr.edumentorhub.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import cr.ac.ucr.edumentorhub.entity.Rol;
import cr.ac.ucr.edumentorhub.entity.Usuario;

@DataJpaTest
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository repo;

    @Test
    void guardaUsuarioYLoBuscaPorEmail() {
        repo.save(new Usuario("C00001", "ana@ucr.ac.cr", "hash", "Ana", "Mora", Rol.ESTUDIANTE));

        var encontrado = repo.findByEmail("ana@ucr.ac.cr");

        assertTrue(encontrado.isPresent());
        assertEquals(Rol.ESTUDIANTE, encontrado.get().getRol());
        assertEquals(0, encontrado.get().getTotalEvaluaciones());
    }

    @Test
    void emailDuplicadoLanzaDataIntegrityViolationException() {
        repo.saveAndFlush(new Usuario("C00001", "ana@ucr.ac.cr", "hash", "Ana", "Mora", Rol.ESTUDIANTE));

        // carné distinto, lo que falla es solo el email
        Usuario duplicado = new Usuario("C00002", "ana@ucr.ac.cr", "hash", "Luis", "Rojas", Rol.TUTOR);

        assertThrows(DataIntegrityViolationException.class, () -> repo.saveAndFlush(duplicado));
    }
}