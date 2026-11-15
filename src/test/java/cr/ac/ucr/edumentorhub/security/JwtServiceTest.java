package cr.ac.ucr.edumentorhub.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.util.ReflectionTestUtils;

import cr.ac.ucr.edumentorhub.entity.Rol;
import cr.ac.ucr.edumentorhub.entity.Usuario;
import io.jsonwebtoken.JwtException;

class JwtServiceTest {

    private static final String SECRETO = "clave-de-prueba-con-mas-de-32-caracteres-123";

    private final JwtService jwtService = new JwtService(SECRETO, 3_600_000);

    private Usuario usuario(String email) {
        Usuario u = new Usuario("C20001", email, "hash", "Luis", "Rojas", Rol.TUTOR);
        ReflectionTestUtils.setField(u, "id", 7L);
        return u;
    }

    @Test
    void elTokenGeneradoContieneElCorreoYEsValido() {
        String token = jwtService.generarToken(usuario("luis@ucr.ac.cr"));

        assertEquals("luis@ucr.ac.cr", jwtService.extraerEmail(token));
        assertTrue(jwtService.esValido(token, new User("luis@ucr.ac.cr", "hash", List.of())));
        assertFalse(jwtService.esValido(token, new User("otro@ucr.ac.cr", "hash", List.of())));
    }

    @Test
    void unTokenAlteradoEsRechazado() {
        String token = jwtService.generarToken(usuario("luis@ucr.ac.cr"));
        String alterado = token.substring(0, token.length() - 2) + "xx";

        assertThrows(JwtException.class, () -> jwtService.extraerEmail(alterado));
    }
}
